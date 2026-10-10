package com.questrings.domain.usecase.tasks

import com.questrings.data.notifications.TaskNotificationScheduler
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskStatus
import com.questrings.domain.repository.StatsRepository
import com.questrings.domain.repository.TaskRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val statsRepository: StatsRepository,
    private val notificationScheduler: TaskNotificationScheduler
) {
    suspend operator fun invoke(taskId: String): Result<Int> = runCatching {
        val task = taskRepository.getTaskById(taskId).getOrNull()
            ?: throw IllegalArgumentException("Task not found")

        val now = System.currentTimeMillis()
        val liveStatus = task.computeLiveStatus(now)
        if (liveStatus == TaskStatus.FAILED) {
            throw IllegalStateException("Cannot complete an expired failed task")
        }

        // Cancel scheduled alerts for completed task
        notificationScheduler.cancelAllForTask(taskId)

        val xpAwarded = taskRepository.completeTask(taskId, now).getOrThrow()

        // Feed XP directly into UserStats
        val currentStats = statsRepository.observeUserStats(task.userId).firstOrNull()
        if (currentStats != null) {
            val newTotalXp = currentStats.xp + xpAwarded
            var level = currentStats.level
            var xpRequiredForNext = level * 500
            while (newTotalXp >= xpRequiredForNext) {
                level++
                xpRequiredForNext += level * 500
            }

            val updatedStats = currentStats.copy(
                xp = newTotalXp,
                level = level,
                totalHabitsCompleted = currentStats.totalHabitsCompleted + 1
            )
            statsRepository.updateStats(updatedStats)
        }

        xpAwarded
    }
}
