package com.questrings.presentation.assignment.verification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.usecase.assignments.ApproveCompletionUseCase
import com.questrings.domain.usecase.assignments.ObservePendingVerificationsUseCase
import com.questrings.domain.usecase.assignments.RejectCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerificationQueueUiState(
    val pendingCompletions: List<AssignmentCompletion> = emptyList(),
    val isLoading: Boolean = true,
    val toastMessage: String? = null
)

@HiltViewModel
class VerificationQueueViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observePendingVerificationsUseCase: ObservePendingVerificationsUseCase,
    private val approveCompletionUseCase: ApproveCompletionUseCase,
    private val rejectCompletionUseCase: RejectCompletionUseCase
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])

    private val _uiState = MutableStateFlow(VerificationQueueUiState())
    val uiState: StateFlow<VerificationQueueUiState> = _uiState.asStateFlow()

    init {
        loadPending()
    }

    private fun loadPending() {
        viewModelScope.launch {
            observePendingVerificationsUseCase(roomId).collect { list ->
                _uiState.update {
                    it.copy(
                        pendingCompletions = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun approve(completion: AssignmentCompletion) {
        viewModelScope.launch {
            approveCompletionUseCase(completion.id, completion.assignmentId).fold(
                onSuccess = { entry ->
                    _uiState.update {
                        it.copy(toastMessage = "Approved ${entry.displayName}! Rank #${entry.rank} assigned (${entry.pointsAwarded} pts)")
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(toastMessage = error.localizedMessage ?: "Failed to approve completion")
                    }
                }
            )
        }
    }

    fun reject(completionId: String, reason: String) {
        viewModelScope.launch {
            rejectCompletionUseCase(completionId, reason.trim()).fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Submission rejected") }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(toastMessage = error.localizedMessage ?: "Failed to reject submission")
                    }
                }
            )
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
