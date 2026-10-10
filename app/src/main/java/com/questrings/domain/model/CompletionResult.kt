package com.questrings.domain.model

data class CompletionResult(
    val xpGained: Int,
    val newTotalXp: Int,
    val didLevelUp: Boolean,
    val newLevel: Int,
    val closedCategory: HabitCategory?,   // null if no ring closed
    val allRingsClosed: Boolean,
    val newStreak: Int,
    val multiplierApplied: Float
)
