package com.questrings.presentation.xpledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.repository.AuthRepository
import com.questrings.domain.repository.XpSourceFilter
import com.questrings.domain.usecase.xp.ObserveXpHistoryUseCase
import com.questrings.domain.usecase.xp.ObserveXpSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class XpLedgerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val observeXpSummaryUseCase: ObserveXpSummaryUseCase,
    private val observeXpHistoryUseCase: ObserveXpHistoryUseCase
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(XpSourceFilter.ALL)
    private val _selectedEvent = MutableStateFlow<XpEvent?>(null)

    val uiState: StateFlow<XpLedgerUiState> = authRepository.currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(XpLedgerUiState(isLoading = false))
        } else {
            val summaryFlow = observeXpSummaryUseCase(user.uid)
            val historyFlow = _selectedFilter.flatMapLatest { filter ->
                observeXpHistoryUseCase(user.uid, filter, limit = 100)
            }

            combine(
                summaryFlow,
                historyFlow,
                _selectedFilter,
                _selectedEvent
            ) { summary, events, filter, selectedEvent ->
                val weeklyStats = calculateWeeklyStats(events)
                XpLedgerUiState(
                    isLoading = false,
                    summary = summary,
                    selectedFilter = filter,
                    events = events,
                    weeklyStats = weeklyStats,
                    selectedEvent = selectedEvent
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = XpLedgerUiState()
    )

    fun setFilter(filter: XpSourceFilter) {
        _selectedFilter.value = filter
    }

    fun selectEvent(event: XpEvent?) {
        _selectedEvent.value = event
    }

    private fun calculateWeeklyStats(events: List<XpEvent>): List<DayXpStat> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayLabelFormat = SimpleDateFormat("EEE", Locale.US)
        val todayKey = dateFormat.format(Date())

        val stats = mutableListOf<DayXpStat>()
        val cal = Calendar.getInstance()

        // 7 days ending today
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            val dateKey = dateFormat.format(c.time)
            val dayLabel = dayLabelFormat.format(c.time).take(1).uppercase()

            // Sum XP for this date
            val dayTotal = events.filter {
                val evDateKey = dateFormat.format(Date(it.earnedAt))
                evDateKey == dateKey
            }.sumOf { it.totalXp }

            stats.add(
                DayXpStat(
                    dayLabel = dayLabel,
                    dateKey = dateKey,
                    xp = dayTotal,
                    isToday = dateKey == todayKey
                )
            )
        }
        return stats
    }
}
