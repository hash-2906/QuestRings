package com.questrings.domain.model

data class UserStats(
    val userId: String = "",
    val xp: Int = 0,
    val xpWeekly: Int = 0,
    val xpMonthly: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: String = "",    // "YYYY-MM-DD"
    val totalHabitsCompleted: Int = 0,
    val xpByCategory: Map<HabitCategory, Int> = mapOf(
        HabitCategory.VITALITY to 0,
        HabitCategory.INTELLECT to 0,
        HabitCategory.DISCIPLINE to 0
    )
)
