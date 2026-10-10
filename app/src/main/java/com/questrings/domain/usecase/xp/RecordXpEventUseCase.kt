package com.questrings.domain.usecase.xp

import com.questrings.domain.model.UserStats
import com.questrings.domain.model.social.FeedEventType
import com.questrings.domain.model.social.SocialFeedEvent
import com.questrings.domain.model.xp.AppEvent
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpEventResult
import com.questrings.domain.model.xp.XpSource
import com.questrings.domain.repository.AuthRepository
import com.questrings.domain.repository.SocialRepository
import com.questrings.domain.repository.StatsRepository
import com.questrings.domain.repository.XpRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject
import kotlin.math.floor

class RecordXpEventUseCase @Inject constructor(
    private val xpRepository: XpRepository,
    private val statsRepository: StatsRepository,
    private val evaluateMedalUseCase: EvaluateMedalUseCase,
    private val appEventBus: MutableSharedFlow<AppEvent>,
    private val socialRepository: SocialRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        userId: String,
        source: XpSource,
        baseXp: Int,
        bonusXp: Int = 0,
        multiplier: Float = 1.0f,
        description: String,
        sourceEntityId: String? = null,
        sourceEntityTitle: String? = null
    ): Result<XpEventResult> = runCatching {
        val currentStats = statsRepository.observeUserStats(userId).firstOrNull() ?: UserStats(userId = userId)
        val currentUser = authRepository.currentUser.firstOrNull()

        val totalPreMultiplier = baseXp + bonusXp
        val totalXp = if (totalPreMultiplier >= 0) {
            floor(totalPreMultiplier * multiplier).toInt()
        } else {
            totalPreMultiplier
        }

        val newTotalXp = (currentStats.xp + totalXp).coerceAtLeast(0)
        val newXpWeekly = (currentStats.xpWeekly + totalXp).coerceAtLeast(0)
        val newXpMonthly = (currentStats.xpMonthly + totalXp).coerceAtLeast(0)
        var level = currentStats.level
        var xpRequiredForNext = level * 500
        var didLevelUp = false

        while (newTotalXp >= xpRequiredForNext) {
            level++
            xpRequiredForNext += level * 500
            didLevelUp = true
        }

        val updatedStats = currentStats.copy(
            xp = newTotalXp,
            xpWeekly = newXpWeekly,
            xpMonthly = newXpMonthly,
            level = level
        )
        statsRepository.updateStats(updatedStats)

        val medalUnlock = evaluateMedalUseCase(
            userId = userId,
            totalXp = newTotalXp,
            level = level
        )

        val event = XpEvent(
            id = UUID.randomUUID().toString(),
            userId = userId,
            source = source,
            baseXp = baseXp,
            bonusXp = bonusXp,
            totalXp = totalXp,
            multiplier = multiplier,
            description = description,
            sourceEntityId = sourceEntityId,
            sourceEntityTitle = sourceEntityTitle,
            earnedAt = System.currentTimeMillis(),
            cumulativeXpAfter = newTotalXp
        )

        xpRepository.recordXpEvent(event).getOrThrow()

        if (didLevelUp) {
            appEventBus.emit(AppEvent.LevelUp(level))
        }

        val playerName = currentUser?.displayName ?: "Adventurer"
        val avatarColor = currentUser?.avatarColorHex ?: "#7E5DFF"

        if (medalUnlock != null) {
            appEventBus.emit(AppEvent.MedalUnlocked(medalUnlock))
            socialRepository.publishFeedEvent(
                SocialFeedEvent(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    displayName = playerName,
                    avatarColorHex = avatarColor,
                    eventType = FeedEventType.MEDAL_UNLOCKED,
                    description = "unlocked ${medalUnlock.tier.displayName} Medal 🏆",
                    timestamp = System.currentTimeMillis(),
                    xpEarned = totalXp
                )
            )
        } else if (source == XpSource.STREAK_MILESTONE) {
            socialRepository.publishFeedEvent(
                SocialFeedEvent(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    displayName = playerName,
                    avatarColorHex = avatarColor,
                    eventType = FeedEventType.STREAK_MILESTONE,
                    description = "reached a new streak milestone! 🔥",
                    timestamp = System.currentTimeMillis(),
                    xpEarned = totalXp
                )
            )
        } else if (source == XpSource.ALL_RINGS_CLOSED) {
            socialRepository.publishFeedEvent(
                SocialFeedEvent(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    displayName = playerName,
                    avatarColorHex = avatarColor,
                    eventType = FeedEventType.ALL_RINGS_CLOSED,
                    description = "closed all 3 rings today! ⭕",
                    timestamp = System.currentTimeMillis(),
                    xpEarned = totalXp
                )
            )
        } else if (source == XpSource.ROOM_ASSIGNMENT_COMPLETION) {
            socialRepository.publishFeedEvent(
                SocialFeedEvent(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    displayName = playerName,
                    avatarColorHex = avatarColor,
                    eventType = FeedEventType.ROOM_ASSIGNMENT_WON,
                    description = "completed quest: ${sourceEntityTitle ?: description}",
                    timestamp = System.currentTimeMillis(),
                    xpEarned = totalXp
                )
            )
        }

        XpEventResult(
            event = event,
            didLevelUp = didLevelUp,
            newLevel = level,
            medalUnlocked = medalUnlock
        )
    }
}
