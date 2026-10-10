package com.questrings.domain.model.assignment

data class RoomLeaderboardEntry(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val avatarColorHex: String,
    val totalPoints: Int,
    val totalXpEarned: Int,
    val assignmentsCompleted: Int,
    val isCurrentUser: Boolean
)
