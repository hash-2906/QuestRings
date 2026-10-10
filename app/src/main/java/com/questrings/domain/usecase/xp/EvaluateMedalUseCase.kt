package com.questrings.domain.usecase.xp

import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.xp.MedalUnlockEvent
import com.questrings.domain.repository.MedalRepository
import javax.inject.Inject

class EvaluateMedalUseCase @Inject constructor(
    private val medalRepository: MedalRepository
) {
    suspend operator fun invoke(
        userId: String,
        totalXp: Int,
        level: Int
    ): MedalUnlockEvent? {
        val qualifiedTier = MedalProgressionConfig.evaluate(totalXp, level)
        val currentTier = medalRepository.getLastUnlockedMedal(userId).getOrDefault(MedalTier.NONE)

        if (qualifiedTier.ordinal > currentTier.ordinal && qualifiedTier != MedalTier.NONE) {
            medalRepository.updateMedal(userId, qualifiedTier)
            val threshold = MedalProgressionConfig.thresholds.first { it.tier == qualifiedTier }
            return MedalUnlockEvent(
                tier = qualifiedTier,
                threshold = threshold,
                previousTier = currentTier
            )
        }
        return null
    }
}
