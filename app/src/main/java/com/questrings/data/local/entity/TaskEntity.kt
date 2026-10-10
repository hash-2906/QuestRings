package com.questrings.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val notes: String,
    val priority: String,       // TaskPriority.name
    val status: String,         // TaskStatus.name
    val createdAt: Long,
    val dueAt: Long,
    val completedAt: Long?,
    val failedAt: Long?,
    val xpAwarded: Int,
    val isSynced: Boolean = false
)

@Entity(
    tableName = "task_reminders",
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["taskId"])]
)
data class TaskReminderEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val minutesBefore: Int,
    val label: String
)
