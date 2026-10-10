package com.questrings.data.local.entity

import androidx.room.Entity

@Entity(tableName = "daily_progress", primaryKeys = ["dateKey", "userId"])
data class DailyProgressEntity(
    val dateKey: String,
    val userId: String,
    val completionPercent: Int,
    val habitsCompleted: Int,
    val habitsTotal: Int
)
