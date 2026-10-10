package com.questrings.domain.model.xp

data class XpEventResult(
    val event: XpEvent,
    val didLevelUp: Boolean,
    val newLevel: Int,
    val medalUnlocked: MedalUnlockEvent?
)
