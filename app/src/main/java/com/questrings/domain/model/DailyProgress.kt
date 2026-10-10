package com.questrings.domain.model

data class DailyProgress(
    val dateKey: String,             // "YYYY-MM-DD"
    val completionPercent: Int,      // 0–100
    val habitsCompleted: Int,
    val habitsTotal: Int
)
