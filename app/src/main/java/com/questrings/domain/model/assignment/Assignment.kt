package com.questrings.domain.model.assignment

data class Assignment(
    val id: String,
    val roomId: String,
    val createdBy: String,          // userId of Admin/Moderator who posted it
    val title: String,
    val description: String,
    val type: AssignmentType,
    val checklistItems: List<ChecklistItem> = emptyList(),   // empty for SIMPLE type
    val status: AssignmentStatus = AssignmentStatus.UPCOMING,
    val requiresVerification: Boolean = false,
    val createdAt: Long,
    val dueAt: Long,
    val totalCompletions: Int = 0,      // denormalised count for leaderboard sizing
    val maxPoints: Int = 100            // top rank always receives this
)
