package com.questrings.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.questrings.data.local.dao.CompletionDao
import com.questrings.data.local.dao.HabitDao
import com.questrings.data.local.dao.StatsDao
import com.questrings.data.local.dao.TaskDao
import com.questrings.data.mapper.toDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class HabitSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao,
    private val statsDao: StatsDao,
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Sync habits
            val unsyncedHabits = habitDao.getUnsyncedHabits()
            val syncedHabitIds = mutableListOf<String>()
            for (habitEntity in unsyncedHabits) {
                firestore.collection("users")
                    .document(habitEntity.userId)
                    .collection("habits")
                    .document(habitEntity.id)
                    .set(habitEntity.toDomain())
                    .await()
                syncedHabitIds.add(habitEntity.id)
            }
            if (syncedHabitIds.isNotEmpty()) {
                habitDao.markSynced(syncedHabitIds)
            }

            // 2. Sync completions
            val unsyncedCompletions = completionDao.getUnsyncedCompletions()
            val syncedCompletionIds = mutableListOf<String>()
            for (completionEntity in unsyncedCompletions) {
                firestore.collection("users")
                    .document(completionEntity.userId)
                    .collection("completions")
                    .document(completionEntity.id)
                    .set(completionEntity.toDomain())
                    .await()
                syncedCompletionIds.add(completionEntity.id)
            }
            if (syncedCompletionIds.isNotEmpty()) {
                completionDao.markSynced(syncedCompletionIds)
            }

            // 3. Sync stats
            val unsyncedStats = statsDao.getUnsyncedStats()
            for (statEntity in unsyncedStats) {
                val stats = statEntity.toDomain()
                val statsMap = mapOf(
                    "userId" to stats.userId,
                    "xp" to stats.xp,
                    "level" to stats.level,
                    "currentStreak" to stats.currentStreak,
                    "longestStreak" to stats.longestStreak,
                    "lastActivityDate" to stats.lastActivityDate,
                    "totalHabitsCompleted" to stats.totalHabitsCompleted,
                    "xpByCategory" to stats.xpByCategory.mapKeys { it.key.name }
                )
                firestore.collection("users")
                    .document(stats.userId)
                    .collection("stats")
                    .document("current")
                    .set(statsMap)
                    .await()
                statsDao.markSynced(stats.userId)
            }

            // 4. Sync tasks
            val unsyncedTasks = taskDao.getUnsyncedTasks()
            val syncedTaskIds = mutableListOf<String>()
            for (taskEntity in unsyncedTasks) {
                val reminders = taskDao.getRemindersForTask(taskEntity.id).map { it.toDomain() }
                val task = taskEntity.toDomain(reminders)
                firestore.collection("users")
                    .document(task.userId)
                    .collection("tasks")
                    .document(task.id)
                    .set(task)
                    .await()
                syncedTaskIds.add(task.id)
            }
            if (syncedTaskIds.isNotEmpty()) {
                taskDao.markTasksSynced(syncedTaskIds)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
