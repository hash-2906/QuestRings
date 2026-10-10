package com.questrings.domain.repository

import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskReminder
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasksForUser(userId: String): Flow<List<Task>>
    fun observeTasksByDateRange(
        userId: String, from: Long, to: Long
    ): Flow<List<Task>>
    suspend fun addTask(task: Task, reminders: List<TaskReminder>): Result<Unit>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun completeTask(taskId: String, completedAt: Long): Result<Int>
    suspend fun failTask(taskId: String): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun getTaskById(taskId: String): Result<Task?>
}
