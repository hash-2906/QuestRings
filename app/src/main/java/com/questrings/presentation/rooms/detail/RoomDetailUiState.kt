package com.questrings.presentation.rooms.detail

import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.usecase.assignments.AssignmentWithStatus

enum class RoomDetailTab(val title: String) {
    ASSIGNMENTS("ASSIGNMENTS"),
    MEMBERS("MEMBERS"),
    LEADERBOARD("LEADERBOARD"),
    REQUESTS("REQUESTS"),
    VERIFY("VERIFY")
}

sealed interface RoomDetailNavigationEffect {
    data object NavigateBack : RoomDetailNavigationEffect
    data class NavigateToShare(val roomId: String) : RoomDetailNavigationEffect
    data class NavigateToAssignmentDetail(val roomId: String, val assignmentId: String) : RoomDetailNavigationEffect
}

data class RoomDetailUiState(
    val isLoading: Boolean = true,
    val room: Room? = null,
    val members: List<RoomMember> = emptyList(),
    val pendingRequests: List<JoinRequest> = emptyList(),
    val assignments: List<AssignmentWithStatus> = emptyList(),
    val pendingVerifications: List<AssignmentCompletion> = emptyList(),
    val currentUserRole: RoomRole = RoomRole.MEMBER,
    val currentUserId: String = "",
    val selectedTab: RoomDetailTab = RoomDetailTab.ASSIGNMENTS,
    val isSettingsOpen: Boolean = false,
    val isCreateAssignmentSheetOpen: Boolean = false,
    val isClosedAssignmentsCollapsed: Boolean = true,
    val toastMessage: String? = null,
    val isLeavingOrDeleting: Boolean = false
)
