package com.questrings.domain.usecase.tasks

import com.questrings.domain.model.Task
import com.questrings.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetTasksForCalendarUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<Map<LocalDate, List<Task>>> {
        return taskRepository.observeTasksForUser(userId).map { tasks ->
            val zoneId = ZoneId.systemDefault()
            val now = System.currentTimeMillis()
            tasks.map { it.copy(status = it.computeLiveStatus(now)) }
                .groupBy { task ->
                    Instant.ofEpochMilli(task.dueAt).atZone(zoneId).toLocalDate()
                }
        }
    }
}
