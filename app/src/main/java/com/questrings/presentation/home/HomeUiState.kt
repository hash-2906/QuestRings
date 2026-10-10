package com.questrings.presentation.home

import com.questrings.core.designsystem.components.RingProgressData
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitWithStatus
import com.questrings.domain.model.User
import com.questrings.domain.model.UserStats
import com.questrings.presentation.home.components.DayMomentum

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
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
    val habits: List<HabitWithStatus> = emptyList(),
    val selectedCategory: HabitCategory? = null,
    val weeklyMomentum: List<DayMomentum> = emptyList(),
    val ringData: RingProgressData = RingProgressData(),
    val toastMessage: String? = null,
    val levelUpEvent: Int? = null, // new level if triggered
    val showConfetti: Boolean = false,
    val isAddHabitSheetOpen: Boolean = false,
    val editingHabit: Habit? = null
) {
    val filteredHabits: List<HabitWithStatus>
        get() = if (selectedCategory == null) habits else habits.filter { it.habit.category == selectedCategory }

    fun getCategoryCount(category: HabitCategory): Pair<Int, Int> {
        val list = habits.filter { it.habit.category == category }
        val done = list.count { it.isDoneToday }
        return Pair(done, list.size)
    }

    val xpForNextLevel: Int
        get() = stats.level * 500

    val currentLevelProgress: Float
        get() = (stats.xp % 500).toFloat() / 500f
}
