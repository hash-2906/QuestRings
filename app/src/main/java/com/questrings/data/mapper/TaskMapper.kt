package com.questrings.data.mapper

import com.questrings.data.local.entity.TaskEntity
import com.questrings.data.local.entity.TaskReminderEntity
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskPriority
import com.questrings.domain.model.TaskReminder
import com.questrings.domain.model.TaskStatus

fun TaskEntity.toDomain(reminders: List<TaskReminder> = emptyList()): Task {
    return Task(
        id = id,
        userId = userId,
        title = title,
        notes = notes,
        priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.MEDIUM },
        status = try { TaskStatus.valueOf(status) } catch (e: Exception) { TaskStatus.UPCOMING },
        createdAt = createdAt,
        dueAt = dueAt,
        completedAt = completedAt,
        failedAt = failedAt,
        reminders = reminders,
        xpAwarded = xpAwarded
    )
}

fun Task.toEntity(isSynced: Boolean = false): TaskEntity {
    return TaskEntity(
        id = id,
        userId = userId,
        title = title,
        notes = notes,
        priority = priority.name,
        status = status.name,
        createdAt = createdAt,
        dueAt = dueAt,
        completedAt = completedAt,
        failedAt = failedAt,
        xpAwarded = xpAwarded,
        isSynced = isSynced
    )
}

fun TaskReminderEntity.toDomain(): TaskReminder {
    return TaskReminder(
        id = id,
        taskId = taskId,
        minutesBefore = minutesBefore,
        label = label
    )
}

fun TaskReminder.toEntity(): TaskReminderEntity {
    return TaskReminderEntity(
        id = id,
        taskId = taskId,
        minutesBefore = minutesBefore,
        label = label
    )
}
