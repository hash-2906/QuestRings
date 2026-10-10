package com.questrings.presentation.assignment.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.assignments.CreateAssignmentUseCase
import com.questrings.domain.usecase.assignments.DeleteAssignmentUseCase
import com.questrings.domain.usecase.assignments.ObserveAssignmentsWithStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeAssignmentsWithStatusUseCase: ObserveAssignmentsWithStatusUseCase,
    private val createAssignmentUseCase: CreateAssignmentUseCase,
    private val deleteAssignmentUseCase: DeleteAssignmentUseCase
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])

    private val _uiState = MutableStateFlow(AssignmentsUiState())
    val uiState: StateFlow<AssignmentsUiState> = _uiState.asStateFlow()

    init {
        loadAssignments()
    }

    private fun loadAssignments() {
        viewModelScope.launch {
            val user = observeCurrentUserUseCase().firstOrNull()
            val userId = user?.uid ?: ""

            observeAssignmentsWithStatusUseCase(roomId, userId).collect { list ->
                val active = list.filter { it.liveStatus != AssignmentStatus.CLOSED }
                val closed = list.filter { it.liveStatus == AssignmentStatus.CLOSED }

                _uiState.update {
                    it.copy(
                        activeAssignments = active,
                        closedAssignments = closed,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun openCreateSheet() {
        _uiState.update { it.copy(isCreateSheetOpen = true) }
    }

    fun closeCreateSheet() {
        _uiState.update { it.copy(isCreateSheetOpen = false) }
    }

    fun toggleClosedCollapsed() {
        _uiState.update { it.copy(isClosedCollapsed = !it.isClosedCollapsed) }
    }

    fun createAssignment(
        title: String,
        description: String,
        type: AssignmentType,
        items: List<String>,
        dueAt: Long,
        requiresVerification: Boolean = false
    ) {
        viewModelScope.launch {
            val user = observeCurrentUserUseCase().firstOrNull()
            val userId = user?.uid ?: ""

            createAssignmentUseCase(
                roomId = roomId,
                createdBy = userId,
                title = title,
                description = description,
                type = type,
                checklistItems = items,
                dueAt = dueAt,
                requiresVerification = requiresVerification
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isCreateSheetOpen = false,
                            toastMessage = "Quest '${it.title}' posted to chamber!"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(toastMessage = error.localizedMessage ?: "Failed to post quest")
                    }
                }
            )
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            deleteAssignmentUseCase(assignmentId).fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Quest removed from chamber") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to delete quest") }
                }
            )
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
