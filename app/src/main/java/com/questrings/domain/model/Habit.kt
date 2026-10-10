package com.questrings.domain.model

data class Habit(
    val id: String,
    val userId: String = "",
    val title: String,
    val goal: String,
    val category: HabitCategory,
    val icon: HabitIcon,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
