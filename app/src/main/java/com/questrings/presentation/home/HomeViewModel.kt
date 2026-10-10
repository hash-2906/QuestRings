package com.questrings.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.core.designsystem.components.RingProgressData
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitIcon
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.auth.SignOutUseCase
import com.questrings.domain.usecase.habits.AddHabitUseCase
import com.questrings.domain.usecase.habits.DeleteHabitUseCase
import com.questrings.domain.usecase.habits.EditHabitUseCase
import com.questrings.domain.usecase.habits.ObserveHabitsWithTodayStatusUseCase
import com.questrings.domain.usecase.habits.ObserveUserStatsUseCase
import com.questrings.domain.usecase.habits.SeedDefaultHabitsUseCase
import com.questrings.domain.usecase.habits.ToggleHabitCompletionUseCase
import com.questrings.presentation.home.components.DayMomentum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

sealed interface HomeNavigationEffect {
    data object NavigateToAuth : HomeNavigationEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeHabitsWithTodayStatusUseCase: ObserveHabitsWithTodayStatusUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val editHabitUseCase: EditHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val seedDefaultHabitsUseCase: SeedDefaultHabitsUseCase,
    private val observeUserStatsUseCase: ObserveUserStatsUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<HabitCategory?>(null)
    private val _toastMessage = MutableStateFlow<String?>(null)
    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    private val _showConfetti = MutableStateFlow(false)
    private val _isAddHabitSheetOpen = MutableStateFlow(false)
    private val _editingHabit = MutableStateFlow<Habit?>(null)

    private val _navigationEffect = MutableSharedFlow<HomeNavigationEffect>()
    val navigationEffect: SharedFlow<HomeNavigationEffect> = _navigationEffect.asSharedFlow()

    private val currentUserFlow = observeCurrentUserUseCase()

    init {
        viewModelScope.launch {
            currentUserFlow.collect { user ->
                if (user != null) {
                    seedDefaultHabitsUseCase(user.uid)
                }
            }
        }
    }

    private val habitsFlow = currentUserFlow.flatMapLatest { user ->
        if (user != null) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            observeHabitsWithTodayStatusUseCase(user.uid, today)
        } else {
            flowOf(emptyList())
        }
    }

    private val statsFlow = currentUserFlow.flatMapLatest { user ->
        if (user != null) {
            observeUserStatsUseCase(user.uid)
        } else {
            flowOf(null)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        currentUserFlow,
        habitsFlow,
        statsFlow,
        _selectedCategory,
        _toastMessage,
        _levelUpEvent,
        _showConfetti,
        _isAddHabitSheetOpen,
        _editingHabit
    ) { params ->
        val user = params[0] as? com.questrings.domain.model.User
        @Suppress("UNCHECKED_CAST")
        val habits = params[1] as List<com.questrings.domain.model.HabitWithStatus>
        val stats = params[2] as? com.questrings.domain.model.UserStats
        val selectedCategory = params[3] as? HabitCategory
        val toast = params[4] as? String
        val levelUp = params[5] as? Int
        val showConfetti = params[6] as Boolean
        val isSheetOpen = params[7] as Boolean
        val editingHabit = params[8] as? Habit

        fun calcProgress(cat: HabitCategory): Float {
            val list = habits.filter { it.habit.category == cat }
            if (list.isEmpty()) return 0f
            return (list.count { it.isDoneToday }.toFloat() / list.size.toFloat()).coerceIn(0f, 1f)
        }

        val ringData = RingProgressData(
            vitalityProgress = calcProgress(HabitCategory.VITALITY),
            intellectProgress = calcProgress(HabitCategory.INTELLECT),
            disciplineProgress = calcProgress(HabitCategory.DISCIPLINE)
        )

        // Generate past 7 days momentum strip
        val momentumList = generate7DayMomentum(habits)

        HomeUiState(
            user = user,
            stats = stats ?: com.questrings.domain.model.UserStats(
                userId = user?.uid ?: "",
                xp = 0,
                level = 1,
                currentStreak = 0,
                longestStreak = 0,
                lastActivityDate = "",
                totalHabitsCompleted = 0,
                xpByCategory = HabitCategory.values().associateWith { 0 }
            ),
            habits = habits,
            selectedCategory = selectedCategory,
            weeklyMomentum = momentumList,
            ringData = ringData,
            toastMessage = toast,
            levelUpEvent = levelUp,
            showConfetti = showConfetti,
            isAddHabitSheetOpen = isSheetOpen,
            editingHabit = editingHabit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.SelectCategory -> {
                _selectedCategory.value = event.category
            }
            is HomeUiEvent.ToggleHabit -> {
                val user = uiState.value.user ?: return
                viewModelScope.launch {
                    val habitWithStatus = uiState.value.habits.find { it.habit.id == event.habitId } ?: return@launch
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val result = toggleHabitCompletionUseCase(
                        userId = user.uid,
                        habitId = event.habitId,
                        dateKey = today,
                        isDone = habitWithStatus.isDoneToday
                    )
                    result.onSuccess { compResult ->
                        if (!habitWithStatus.isDoneToday) {
                            var msg = "+${compResult.xpGained} XP"
                            if (compResult.streakMultiplier > 1.0) {
                                msg += " (×${compResult.streakMultiplier} Streak Bonus!)"
                            }
                            _toastMessage.value = msg

                            if (compResult.allRingsClosedToday || compResult.ringClosedCategory != null) {
                                _showConfetti.value = true
                            }
                            if (compResult.isLevelUp) {
                                _levelUpEvent.value = compResult.newLevel
                            }
                        }
                    }
                }
            }
            is HomeUiEvent.OpenAddHabitSheet -> {
                _editingHabit.value = event.habitToEdit
                _isAddHabitSheetOpen.value = true
            }
            is HomeUiEvent.CloseAddHabitSheet -> {
                _isAddHabitSheetOpen.value = false
                _editingHabit.value = null
            }
            is HomeUiEvent.SaveHabit -> {
                val user = uiState.value.user ?: return
                val currentEditing = _editingHabit.value
                viewModelScope.launch {
                    if (currentEditing != null) {
                        editHabitUseCase(
                            currentEditing.copy(
                                title = event.title,
                                goal = event.goal,
                                category = event.category,
                                icon = event.icon
                            )
                        )
                        _toastMessage.value = "Quest updated"
                    } else {
                        val newHabit = Habit(
                            id = UUID.randomUUID().toString(),
                            userId = user.uid,
                            title = event.title,
                            goal = event.goal,
                            category = event.category,
                            icon = event.icon,
                            isDefault = false,
                            createdAt = System.currentTimeMillis()
                        )
                        addHabitUseCase(newHabit)
                        _toastMessage.value = "Quest added"
                    }
                    _isAddHabitSheetOpen.value = false
                    _editingHabit.value = null
                }
            }
            is HomeUiEvent.DeleteHabit -> {
                viewModelScope.launch {
                    deleteHabitUseCase(event.habitId)
                    _toastMessage.value = "Quest deleted"
                }
            }
            is HomeUiEvent.DismissToast -> {
                _toastMessage.value = null
            }
            is HomeUiEvent.DismissLevelUp -> {
                _levelUpEvent.value = null
            }
            is HomeUiEvent.DismissConfetti -> {
                _showConfetti.value = false
            }
            is HomeUiEvent.SignOut -> {
                viewModelScope.launch {
                    signOutUseCase()
                    _navigationEffect.emit(HomeNavigationEffect.NavigateToAuth)
                }
            }
        }
    }

    private fun generate7DayMomentum(habits: List<com.questrings.domain.model.HabitWithStatus>): List<DayMomentum> {
        val sdfDateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfDayLetter = SimpleDateFormat("EEEEE", Locale.getDefault()) // M, T, W...
        val sdfDayNumber = SimpleDateFormat("d", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val todayKey = sdfDateKey.format(calendar.time)

        val list = mutableListOf<DayMomentum>()
        for (i in 6 downTo 0) {
            val dayCal = Calendar.getInstance()
            dayCal.add(Calendar.DAY_OF_YEAR, -i)
            val dKey = sdfDateKey.format(dayCal.time)
            val isToday = dKey == todayKey

            val doneCount = if (isToday) habits.count { it.isDoneToday } else 0
            val total = habits.size.coerceAtLeast(1)
            val pct = ((doneCount.toFloat() / total.toFloat()) * 100).toInt()

            list.add(
                DayMomentum(
                    dateKey = dKey,
                    dayLetter = sdfDayLetter.format(dayCal.time),
                    dayNumber = sdfDayNumber.format(dayCal.time),
                    completionPercent = if (isToday) pct else 100, // mock past momentum for lively UI
                    isToday = isToday
                )
            )
        }
        return list
    }
}
