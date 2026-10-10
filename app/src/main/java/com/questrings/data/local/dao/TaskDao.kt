package com.questrings.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.questrings.data.local.entity.TaskEntity
import com.questrings.data.local.entity.TaskReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueAt ASC")
    fun observeAll(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueAt BETWEEN :from AND :to ORDER BY dueAt ASC")
    fun observeByDateRange(userId: String, from: Long, to: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task_reminders WHERE taskId = :taskId")
    suspend fun getRemindersForTask(taskId: String): List<TaskReminderEntity>

    @Query("SELECT * FROM task_reminders WHERE taskId IN (:taskIds)")
    suspend fun getRemindersForTasks(taskIds: List<String>): List<TaskReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<TaskReminderEntity>)

    @Query("DELETE FROM task_reminders WHERE taskId = :taskId")
    suspend fun deleteRemindersForTask(taskId: String)

    @Query("UPDATE tasks SET status = :status, isSynced = 0 WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE tasks SET status = 'COMPLETED', completedAt = :completedAt, xpAwarded = :xpAwarded, isSynced = 0 WHERE id = :id")
    suspend fun updateCompleted(id: String, completedAt: Long, xpAwarded: Int)

    @Query("UPDATE tasks SET status = 'FAILED', failedAt = :failedAt, isSynced = 0 WHERE id = :id")
    suspend fun updateFailed(id: String, failedAt: Long)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND status != 'FAILED' AND :now > dueAt + 86400000")
    suspend fun getExpiredTasksPastGrace(now: Long): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    @Query("UPDATE tasks SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markTasksSynced(ids: List<String>)
}
