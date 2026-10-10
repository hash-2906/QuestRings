package com.questrings.domain.model.xp

import com.questrings.domain.config.MedalThreshold
import com.questrings.domain.model.medal.MedalTier

data class MedalUnlockEvent(
    val tier: MedalTier,
    val threshold: MedalThreshold,
    val previousTier: MedalTier
)
