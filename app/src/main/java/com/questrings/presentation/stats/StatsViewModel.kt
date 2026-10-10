package com.questrings.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.DailyProgress
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.UserStats
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.habits.GetDailyProgressHistoryUseCase
import com.questrings.domain.usecase.habits.ObserveUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeUserStatsUseCase: ObserveUserStatsUseCase,
    private val getDailyProgressHistoryUseCase: GetDailyProgressHistoryUseCase
) : ViewModel() {

    private val _historyFlow = MutableStateFlow<List<DailyProgress>>(emptyList())

    private val currentUserFlow = observeCurrentUserUseCase()

    init {
        viewModelScope.launch {
            currentUserFlow.collect { user ->
                if (user != null) {
                    loadHistory(user.uid)
                }
            }
        }
    }

    private suspend fun loadHistory(userId: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val toDate = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -91)
        val fromDate = sdf.format(cal.time)

        getDailyProgressHistoryUseCase(userId, fromDate, toDate).onSuccess { list ->
            // If empty in Room, generate placeholder distribution based on streak
            val generated = if (list.isEmpty()) {
                generatePlaceholderHeatmap()
            } else {
                list
            }
            _historyFlow.value = generated
        }
    }

    private fun generatePlaceholderHeatmap(): List<DailyProgress> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val list = mutableListOf<DailyProgress>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -90)
        for (i in 0..90) {
            val dKey = sdf.format(cal.time)
            // Livelier demo matrix
            val pct = when {
                i > 75 -> 100
                i % 4 == 0 -> 80
                i % 7 == 0 -> 40
                i % 3 == 0 -> 60
                else -> 0
            }
            list.add(
                DailyProgress(
                    dateKey = dKey,
                    completionPercent = pct,
                    habitsCompleted = (pct / 33),
                    habitsTotal = 3
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    private val statsFlow = currentUserFlow.flatMapLatest { user ->
        if (user != null) {
            observeUserStatsUseCase(user.uid)
        } else {
            flowOf(null)
        }
    }

    val uiState: StateFlow<StatsUiState> = combine(
        statsFlow,
        _historyFlow
    ) { stats, history ->
        StatsUiState(
            isLoading = false,
            stats = stats ?: UserStats(
                userId = "",
                xp = 0,
                level = 1,
                currentStreak = 0,
                longestStreak = 0,
                lastActivityDate = "",
                totalHabitsCompleted = 0,
                xpByCategory = HabitCategory.values().associateWith { 0 }
            ),
            heatmapHistory = history
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState(isLoading = true)
    )
}
