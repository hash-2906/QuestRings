package com.questrings.domain.model.xp

import com.questrings.domain.config.MedalThreshold
import com.questrings.domain.model.medal.MedalTier

data class XpSummary(
    val totalXp: Int,
    val level: Int,
    val currentMedal: MedalTier,
    val nextMedal: MedalThreshold?,
    val progressToNextMedal: Float,   // 0f–1f (based on XP gap to next)
    val xpThisWeek: Int,
    val xpBySource: Map<XpSource, Int>
)
