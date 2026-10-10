package com.questrings.domain.model

data class UserProfile(
    val level: Int = 7,
    val rankTitle: String = "Guardian",
    val currentXp: Int = 540,
    val xpForNextLevel: Int = 1000,
    val streakDays: Int = 12,
    val weeklyHistory: List<Int> = listOf(45, 75, 100, 62, 82, 100, 0)
)
