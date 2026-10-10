package com.questrings.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val goal: String,
    val category: String,       // HabitCategory.name
    val icon: String,           // HabitIcon.name
    val isDefault: Boolean,
    val createdAt: Long,
    val sortOrder: Int,
    val isSynced: Boolean = false
)
