package com.questrings.domain.model.room

enum class JoinRequestStatus { PENDING, APPROVED, REJECTED }

data class JoinRequest(
    val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val displayName: String = "",
    val avatarColorHex: String = "#7E5DFF",
    val requestedAt: Long = System.currentTimeMillis(),
    val status: JoinRequestStatus = JoinRequestStatus.PENDING
)
