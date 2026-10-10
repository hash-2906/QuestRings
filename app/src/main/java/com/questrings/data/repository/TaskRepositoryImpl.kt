package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.questrings.data.local.dao.TaskDao
import com.questrings.data.mapper.toDomain
import com.questrings.data.mapper.toEntity
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskReminder
import com.questrings.domain.model.TaskStatus
import com.questrings.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore
) : TaskRepository {

    override fun observeTasksForUser(userId: String): Flow<List<Task>> {
        return taskDao.observeAll(userId).map { entities ->
            val taskIds = entities.map { it.id }
            val allReminders = if (taskIds.isNotEmpty()) taskDao.getRemindersForTasks(taskIds) else emptyList()
            val reminderMap = allReminders.groupBy { it.taskId }

            entities.map { entity ->
                val reminders = reminderMap[entity.id]?.map { it.toDomain() } ?: emptyList()
                entity.toDomain(reminders)
            }
        }
    }

    override fun observeTasksByDateRange(userId: String, from: Long, to: Long): Flow<List<Task>> {
        return taskDao.observeByDateRange(userId, from, to).map { entities ->
            val taskIds = entities.map { it.id }
            val allReminders = if (taskIds.isNotEmpty()) taskDao.getRemindersForTasks(taskIds) else emptyList()
            val reminderMap = allReminders.groupBy { it.taskId }

            entities.map { entity ->
                val reminders = reminderMap[entity.id]?.map { it.toDomain() } ?: emptyList()
                entity.toDomain(reminders)
            }
        }
    }

    override suspend fun addTask(task: Task, reminders: List<TaskReminder>): Result<Unit> = runCatching {
        taskDao.upsertTask(task.toEntity(isSynced = false))
        if (reminders.isNotEmpty()) {
            taskDao.deleteRemindersForTask(task.id)
            taskDao.insertReminders(reminders.map { it.toEntity() })
        }
        try {
            firestore.collection("users")
                .document(task.userId)
                .collection("tasks")
                .document(task.id)
                .set(task)
                .await()
            taskDao.markTasksSynced(listOf(task.id))
        } catch (_: Exception) {
            // Synced via worker
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> = runCatching {
        taskDao.upsertTask(task.toEntity(isSynced = false))
        if (task.reminders.isNotEmpty()) {
            taskDao.deleteRemindersForTask(task.id)
            taskDao.insertReminders(task.reminders.map { it.toEntity() })
        }
        try {
            firestore.collection("users")
                .document(task.userId)
                .collection("tasks")
                .document(task.id)
                .set(task)
                .await()
            taskDao.markTasksSynced(listOf(task.id))
        } catch (_: Exception) {
            // Synced via worker
        }
    }

    override suspend fun completeTask(taskId: String, completedAt: Long): Result<Int> = runCatching {
        val task = taskDao.getTaskById(taskId) ?: throw IllegalArgumentException("Task not found")
        val priorityXp = com.questrings.domain.model.TaskPriority.valueOf(task.priority).xpReward
        val xpAwarded = if (completedAt > task.dueAt) (priorityXp * 0.5f).toInt() else priorityXp

        taskDao.updateCompleted(taskId, completedAt, xpAwarded)
        try {
            firestore.collection("users")
                .document(task.userId)
                .collection("tasks")
                .document(taskId)
                .update(
                    mapOf(
                        "status" to TaskStatus.COMPLETED.name,
                        "completedAt" to completedAt,
                        "xpAwarded" to xpAwarded
                    )
                )
                .await()
            taskDao.markTasksSynced(listOf(taskId))
        } catch (_: Exception) {
            // Synced via worker
        }
        xpAwarded
    }

    override suspend fun failTask(taskId: String): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        taskDao.updateFailed(taskId, now)
        val task = taskDao.getTaskById(taskId)
        if (task != null) {
            try {
                firestore.collection("users")
                    .document(task.userId)
                    .collection("tasks")
                    .document(taskId)
                    .update(
                        mapOf(
                            "status" to TaskStatus.FAILED.name,
                            "failedAt" to now
                        )
                    )
                    .await()
                taskDao.markTasksSynced(listOf(taskId))
            } catch (_: Exception) {
                // Background sync
            }
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
        val task = taskDao.getTaskById(taskId)
        taskDao.deleteTask(taskId)
        if (task != null) {
            try {
                firestore.collection("users")
                    .document(task.userId)
                    .collection("tasks")
                    .document(taskId)
                    .delete()
                    .await()
            } catch (_: Exception) {
                // Background sync
            }
        }
    }

    override suspend fun getTaskById(taskId: String): Result<Task?> = runCatching {
        val entity = taskDao.getTaskById(taskId) ?: return@runCatching null
        val reminders = taskDao.getRemindersForTask(taskId).map { it.toDomain() }
        entity.toDomain(reminders)
    }
}
