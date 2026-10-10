package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.questrings.data.local.dao.XpEventDao
import com.questrings.data.mapper.toDomain
import com.questrings.data.mapper.toEntity
import com.questrings.data.mapper.toMap
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSource
import com.questrings.domain.model.xp.XpSummary
import com.questrings.domain.repository.StatsRepository
import com.questrings.domain.repository.XpRepository
import com.questrings.domain.repository.XpSourceFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XpRepositoryImpl @Inject constructor(
    private val xpEventDao: XpEventDao,
    private val statsRepository: StatsRepository,
    private val firestore: FirebaseFirestore
) : XpRepository {

    override fun observeXpSummary(userId: String): Flow<XpSummary> {
        return combine(
            statsRepository.observeUserStats(userId),
            xpEventDao.observeAllEvents(userId)
        ) { stats, eventEntities ->
            val events = eventEntities.map { it.toDomain() }
            val currentMedal = MedalProgressionConfig.evaluate(stats.xp, stats.level)
            val nextMedal = MedalProgressionConfig.nextThreshold(stats.xp, stats.level)

            val progressToNextMedal = if (nextMedal == null) {
                1.0f
            } else {
                val prevReqXp = MedalProgressionConfig.thresholds
                    .filter { it.requiredXp < nextMedal.requiredXp }
                    .maxByOrNull { it.requiredXp }?.requiredXp ?: 0
                val totalSpan = (nextMedal.requiredXp - prevReqXp).coerceAtLeast(1)
                val currentSpan = (stats.xp - prevReqXp).coerceAtLeast(0)
                (currentSpan.toFloat() / totalSpan.toFloat()).coerceIn(0f, 1f)
            }

            val oneWeekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            val xpThisWeek = events
                .filter { it.earnedAt >= oneWeekAgo }
                .sumOf { it.totalXp }

            val xpBySource = XpSource.values().associateWith { source ->
                events.filter { it.source == source }.sumOf { it.totalXp }
            }

            XpSummary(
                totalXp = stats.xp,
                level = stats.level,
                currentMedal = currentMedal,
                nextMedal = nextMedal,
                progressToNextMedal = progressToNextMedal,
                xpThisWeek = xpThisWeek,
                xpBySource = xpBySource
            )
        }
    }

    override fun observeXpHistory(
        userId: String,
        filter: XpSourceFilter,
        limit: Int
    ): Flow<List<XpEvent>> {
        val flow = when (filter) {
            XpSourceFilter.ALL -> xpEventDao.observeEvents(userId, limit)
            XpSourceFilter.HABITS -> xpEventDao.observeEventsBySources(
                userId,
                listOf(
                    XpSource.HABIT_COMPLETION.name,
                    XpSource.RING_CLOSED.name,
                    XpSource.ALL_RINGS_CLOSED.name
                ),
                limit
            )
            XpSourceFilter.TASKS -> xpEventDao.observeEventsBySources(
                userId,
                listOf(XpSource.TASK_COMPLETION.name),
                limit
            )
            XpSourceFilter.ROOMS -> xpEventDao.observeEventsBySources(
                userId,
                listOf(XpSource.ROOM_ASSIGNMENT_COMPLETION.name),
                limit
            )
        }
        return flow.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun recordXpEvent(event: XpEvent): Result<Unit> = runCatching {
        xpEventDao.insertEvent(event.toEntity(isSynced = false))
        try {
            firestore.collection("users")
                .document(event.userId)
                .collection("xp_events")
                .document(event.id)
                .set(event.toMap())
                .await()
            xpEventDao.markSynced(listOf(event.id))
        } catch (_: Exception) {
            // Synced via background sync
        }
    }

    override suspend fun getXpEventById(eventId: String): Result<XpEvent?> = runCatching {
        xpEventDao.getEventById(eventId)?.toDomain()
    }
}
