package com.questrings.domain.usecase.tasks

import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskStatus
import com.questrings.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveTasksWithLiveStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.observeTasksForUser(userId).map { tasks ->
            val now = System.currentTimeMillis()
            tasks.map { task ->
                val liveStatus = task.computeLiveStatus(now)
                task.copy(status = liveStatus)
            }
        }
    }
}
