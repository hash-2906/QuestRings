package com.questrings.domain.model.xp

data class XpEvent(
    val id: String,
    val userId: String,
    val source: XpSource,
    val baseXp: Int,
    val bonusXp: Int,             // streak multiplier bonus / speed bonus
    val totalXp: Int,             // baseXp + bonusXp
    val multiplier: Float,        // streak multiplier applied (1.0 if none)
    val description: String,      // human-readable: "Morning Run · ×1.5 streak"
    val sourceEntityId: String?,  // habitId / taskId / assignmentId
    val sourceEntityTitle: String?,
    val earnedAt: Long,
    val cumulativeXpAfter: Int    // snapshot of total XP after this event
)
