package com.questrings.domain.model

data class HabitWithStatus(
    val habit: Habit,
    val isDoneToday: Boolean
)
