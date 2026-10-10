package com.questrings.domain.model.room

data class RoomMember(
    val userId: String = "",
    val roomId: String = "",
    val displayName: String = "",
    val avatarColorHex: String = "#7E5DFF",
    val role: RoomRole = RoomRole.MEMBER,
    val joinedAt: Long = System.currentTimeMillis(),
    val totalXpEarned: Int = 0,      // XP earned specifically in this room
    val assignmentsCompleted: Int = 0
)
