package com.questrings.domain.model.xp

sealed interface AppEvent {
    data class MedalUnlocked(val event: MedalUnlockEvent) : AppEvent
    data class LevelUp(val newLevel: Int) : AppEvent
}
