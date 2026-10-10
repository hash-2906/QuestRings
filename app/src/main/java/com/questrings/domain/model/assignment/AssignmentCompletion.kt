package com.questrings.domain.model.assignment

data class AssignmentCompletion(
    val id: String,
    val assignmentId: String,
    val roomId: String,
    val userId: String,
    val displayName: String,
    val avatarColorHex: String,
    val completedAt: Long,
    val checkedItemIds: List<String> = emptyList(),   // for CHECKLIST type
    val isLate: Boolean = false,                      // completed during grace period
    val verificationStatus: CompletionVerificationStatus = CompletionVerificationStatus.AUTO_APPROVED,
    val rank: Int? = null,                            // null until verified/auto-approved
    val pointsAwarded: Int? = null,                   // null until rank assigned
    val xpAwarded: Int? = null,                       // null until rank assigned
    val speedBonusAwarded: Int? = null,
    val rejectionReason: String? = null
)
