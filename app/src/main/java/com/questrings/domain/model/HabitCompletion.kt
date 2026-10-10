package com.questrings.domain.model

data class HabitCompletion(
    val id: String,
    val habitId: String,
    val userId: String,
    val completedAt: Long = System.currentTimeMillis(),
    val dateKey: String      // "YYYY-MM-DD"
)
