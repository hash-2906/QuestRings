package com.questrings.presentation.xpledger

import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSummary
import com.questrings.domain.repository.XpSourceFilter

data class DayXpStat(
    val dayLabel: String,  // "M", "T", "W", "T", "F", "S", "S"
    val dateKey: String,   // "YYYY-MM-DD"
    val xp: Int,
    val isToday: Boolean
)

data class XpLedgerUiState(
    val isLoading: Boolean = true,
    val summary: XpSummary? = null,
    val selectedFilter: XpSourceFilter = XpSourceFilter.ALL,
    val events: List<XpEvent> = emptyList(),
    val weeklyStats: List<DayXpStat> = emptyList(),
    val selectedEvent: XpEvent? = null
)
