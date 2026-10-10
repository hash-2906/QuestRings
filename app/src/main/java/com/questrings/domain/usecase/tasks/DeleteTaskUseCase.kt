package com.questrings.domain.usecase.tasks

import com.questrings.data.notifications.TaskNotificationScheduler
import com.questrings.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val notificationScheduler: TaskNotificationScheduler
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        notificationScheduler.cancelAllForTask(taskId)
        return taskRepository.deleteTask(taskId)
    }
}
