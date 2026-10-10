package com.questrings.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String,
    val xp: Int,
    val xpWeekly: Int = 0,
    val xpMonthly: Int = 0,
    val level: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String,
    val totalHabitsCompleted: Int,
    val xpVitality: Int,
    val xpIntellect: Int,
    val xpDiscipline: Int,
    val isSynced: Boolean = false
)
