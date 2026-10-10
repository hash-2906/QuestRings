package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.questrings.data.local.dao.DailyProgressDao
import com.questrings.data.local.dao.StatsDao
import com.questrings.data.mapper.toDomain
import com.questrings.data.mapper.toEntity
import com.questrings.domain.model.DailyProgress
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.UserStats
import com.questrings.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao,
    private val dailyProgressDao: DailyProgressDao,
    private val firestore: FirebaseFirestore
) : StatsRepository {

    override fun observeUserStats(userId: String): Flow<UserStats> {
        return statsDao.observeUserStats(userId).map { entity ->
            entity?.toDomain() ?: UserStats(
                userId = userId,
                xp = 0,
                level = 1,
                currentStreak = 0,
                longestStreak = 0,
                lastActivityDate = "",
                totalHabitsCompleted = 0,
                xpByCategory = HabitCategory.values().associateWith { 0 }
            )
        }
    }

    override suspend fun updateStats(stats: UserStats): Result<Unit> = runCatching {
        statsDao.insertOrUpdateStats(stats.toEntity(isSynced = false))
        try {
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
        } catch (_: Exception) {
            // Synced via worker
        }
    }

    override suspend fun getDailyProgressHistory(
        userId: String,
        from: String,
        to: String
    ): Result<List<DailyProgress>> = runCatching {
        dailyProgressDao.getDailyProgressHistory(userId, from, to).map { it.toDomain() }
    }
}
