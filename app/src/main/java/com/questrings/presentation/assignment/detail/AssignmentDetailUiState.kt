package com.questrings.presentation.assignment.detail

import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.LeaderboardEntry

data class AssignmentDetailUiState(
    val assignment: Assignment? = null,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val currentUserCompletion: AssignmentCompletion? = null,
    val checkedItemIds: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val liveStatus: AssignmentStatus = AssignmentStatus.UPCOMING,
    val timeRemainingStr: String = "",
    val toastMessage: String? = null
)

sealed interface AssignmentDetailUiEvent {
    data class ShowXpToast(val xp: Int, val points: Int, val rank: Int) : AssignmentDetailUiEvent
    data class ShowSnackbar(val message: String) : AssignmentDetailUiEvent
}
