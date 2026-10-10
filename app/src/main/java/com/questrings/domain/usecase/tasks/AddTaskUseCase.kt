package com.questrings.domain.usecase.tasks

import com.questrings.data.notifications.TaskNotificationScheduler
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskReminder
import com.questrings.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationScheduler: TaskNotificationScheduler
) {
    suspend operator fun invoke(task: Task, reminders: List<TaskReminder>): Result<Unit> {
        val now = System.currentTimeMillis()
        if (task.dueAt <= now) {
            return Result.failure(IllegalArgumentException("Due time must be in the future"))
        }

        val result = taskRepository.addTask(task, reminders)
        if (result.isSuccess) {
            notificationScheduler.scheduleReminders(task, reminders)
            notificationScheduler.scheduleDueNowNotification(task)
            notificationScheduler.scheduleGraceExpiry(task)
        }
        return result
    }
}
