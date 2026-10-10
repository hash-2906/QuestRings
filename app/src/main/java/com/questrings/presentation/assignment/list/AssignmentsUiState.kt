package com.questrings.presentation.assignment.list

import com.questrings.domain.usecase.assignments.AssignmentWithStatus

data class AssignmentsUiState(
    val activeAssignments: List<AssignmentWithStatus> = emptyList(),
    val closedAssignments: List<AssignmentWithStatus> = emptyList(),
    val isLoading: Boolean = true,
    val isCreateSheetOpen: Boolean = false,
    val isClosedCollapsed: Boolean = true,
    val toastMessage: String? = null
)
