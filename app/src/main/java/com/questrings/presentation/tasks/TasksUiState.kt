package com.questrings.presentation.tasks

import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskCountdown
import com.questrings.domain.model.TaskReminder
import com.questrings.domain.model.TaskStatus
import java.time.LocalDate

enum class TaskViewMode { LIST, CALENDAR }

enum class TaskStatusFilter { ALL, ACTIVE, COMPLETED, FAILED }

data class TaskWithLiveStatus(
    val task: Task,
    val liveStatus: TaskStatus,
    val countdown: TaskCountdown
)

data class TasksUiState(
    val tasks: List<TaskWithLiveStatus> = emptyList(),
    val viewMode: TaskViewMode = TaskViewMode.LIST,
    val selectedDate: LocalDate = LocalDate.now(),
    val filterStatus: TaskStatusFilter = TaskStatusFilter.ACTIVE,
    val isLoading: Boolean = true,
    val pendingReminders: List<TaskReminder> = emptyList(),
    val isAddTaskSheetOpen: Boolean = false,
    val toastMessage: String? = null,
    val snackbarMessage: String? = null,
    val lastDeletedTask: Task? = null
) {
    val filteredTasks: List<TaskWithLiveStatus>
        get() = when (filterStatus) {
            TaskStatusFilter.ALL -> tasks
            TaskStatusFilter.ACTIVE -> tasks.filter {
                it.liveStatus == TaskStatus.ACTIVE || it.liveStatus == TaskStatus.UPCOMING || it.liveStatus == TaskStatus.GRACE_PERIOD
            }
            TaskStatusFilter.COMPLETED -> tasks.filter { it.liveStatus == TaskStatus.COMPLETED }
            TaskStatusFilter.FAILED -> tasks.filter { it.liveStatus == TaskStatus.FAILED }
        }

    val tasksForSelectedDate: List<TaskWithLiveStatus>
        get() {
            val zone = java.time.ZoneId.systemDefault()
            return tasks.filter {
                java.time.Instant.ofEpochMilli(it.task.dueAt).atZone(zone).toLocalDate() == selectedDate
            }
        }
}
