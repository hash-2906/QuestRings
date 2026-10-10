package com.questrings.domain.model.social

data class FriendRequest(
    val id: String,
    val fromUserId: String,
    val fromDisplayName: String,
    val fromAvatarColorHex: String,
    val sentAt: Long
)
