package com.questrings.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_completions")
data class HabitCompletionEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val userId: String,
    val completedAt: Long,
    val dateKey: String,
    val isSynced: Boolean = false
)
