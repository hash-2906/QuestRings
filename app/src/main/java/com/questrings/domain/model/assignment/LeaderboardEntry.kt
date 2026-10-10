package com.questrings.domain.model.assignment

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val avatarColorHex: String,
    val completedAt: Long,
    val pointsAwarded: Int,
    val xpAwarded: Int,
    val isLate: Boolean,
    val speedBonusAwarded: Int,
    val isCurrentUser: Boolean          // true for the viewing user's own entry
)
