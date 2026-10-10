package com.questrings.presentation.tasks

import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskPriority
import com.questrings.domain.model.TaskReminder
import java.time.LocalDate

sealed interface TasksUiEvent {
    data class SetViewMode(val mode: TaskViewMode) : TasksUiEvent
    data class SetFilter(val filter: TaskStatusFilter) : TasksUiEvent
    data class SelectDate(val date: LocalDate) : TasksUiEvent
    data object OpenAddTaskSheet : TasksUiEvent
    data object CloseAddTaskSheet : TasksUiEvent
    data class CreateTask(
        val title: String,
        val notes: String,
        val priority: TaskPriority,
        val dueAt: Long,
        val reminders: List<TaskReminder>
    ) : TasksUiEvent
    data class CompleteTask(val taskId: String) : TasksUiEvent
    data class DeleteTask(val task: Task) : TasksUiEvent
    data object UndoDeleteTask : TasksUiEvent
    data object DismissToast : TasksUiEvent
    data object DismissSnackbar : TasksUiEvent
}
