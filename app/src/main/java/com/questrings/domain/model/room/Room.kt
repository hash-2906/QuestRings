package com.questrings.domain.model.room

enum class RoomPrivacy { PUBLIC, PRIVATE }

enum class RoomRole {
    ADMIN,       // creator — full control
    MODERATOR,   // can create/manage assignments, remove members
    MEMBER       // can view/complete assignments
}

data class Room(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val emoji: String = "🎯",           // single emoji chosen as room avatar
    val privacy: RoomPrivacy = RoomPrivacy.PUBLIC,
    val joinCode: String = "",        // 6-char alphanumeric e.g. "X7K2QR"
    val creatorId: String = "",
    val maxMembers: Int = 50,         // 5–200
    val memberCount: Int = 1,
    val activeAssignmentCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true        // false = closed/archived room
)
