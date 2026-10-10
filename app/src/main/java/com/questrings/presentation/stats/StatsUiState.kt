package com.questrings.presentation.stats

import com.questrings.domain.model.DailyProgress
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.UserStats

data class StatsUiState(
    val isLoading: Boolean = false,
    val stats: UserStats = UserStats(
        userId = "",
        xp = 0,
        level = 1,
        currentStreak = 0,
        longestStreak = 0,
        lastActivityDate = "",
        totalHabitsCompleted = 0,
        xpByCategory = HabitCategory.values().associateWith { 0 }
    ),
    val heatmapHistory: List<DailyProgress> = emptyList(),
    val errorMessage: String? = null
)
