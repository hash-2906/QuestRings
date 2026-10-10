package com.questrings.presentation.assignment.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.repository.AssignmentRepository
import com.questrings.domain.usecase.assignments.ObserveAssignmentLeaderboardUseCase
import com.questrings.domain.usecase.assignments.ObserveAssignmentsWithStatusUseCase
import com.questrings.domain.usecase.assignments.SubmitCompletionUseCase
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val assignmentRepository: AssignmentRepository,
    private val observeAssignmentLeaderboardUseCase: ObserveAssignmentLeaderboardUseCase,
    private val submitCompletionUseCase: SubmitCompletionUseCase
) : ViewModel() {

    private val assignmentId: String = checkNotNull(savedStateHandle["assignmentId"])

    private val _uiState = MutableStateFlow(AssignmentDetailUiState())
    val uiState: StateFlow<AssignmentDetailUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AssignmentDetailUiEvent>()
    val eventFlow: SharedFlow<AssignmentDetailUiEvent> = _eventFlow.asSharedFlow()

    init {
        loadData()
        startCountdownTicker()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = observeCurrentUserUseCase().firstOrNull()
            val userId = user?.uid ?: ""

            // Observe assignment
            launch {
                assignmentRepository.observeAssignmentById(assignmentId).collect { assignment ->
                    if (assignment != null) {
                        val status = ObserveAssignmentsWithStatusUseCase.computeLiveStatus(assignment.dueAt)
                        _uiState.update {
                            it.copy(
                                assignment = assignment,
                                liveStatus = status
                            )
                        }
                    }
                }
            }

            // Observe user completion
            launch {
                val completion = assignmentRepository.getCurrentUserCompletion(assignmentId, userId).getOrNull()
                _uiState.update { it.copy(currentUserCompletion = completion) }
            }

            // Observe leaderboard
            launch {
                observeAssignmentLeaderboardUseCase(assignmentId, userId).collect { list ->
                    _uiState.update { it.copy(leaderboard = list) }
                }
            }
        }
    }

    private fun startCountdownTicker() {
        viewModelScope.launch {
            while (true) {
                val assignment = _uiState.value.assignment
                if (assignment != null) {
                    val now = System.currentTimeMillis()
                    val liveStatus = ObserveAssignmentsWithStatusUseCase.computeLiveStatus(assignment.dueAt, now)
                    val timeRemaining = formatCountdown(assignment.dueAt, now, liveStatus)
                    _uiState.update {
                        it.copy(
                            liveStatus = liveStatus,
                            timeRemainingStr = timeRemaining
                        )
                    }
                }
                delay(1000)
            }
        }
    }

    private fun formatCountdown(dueAt: Long, now: Long, status: AssignmentStatus): String {
        return when (status) {
            AssignmentStatus.CLOSED -> "CLOSED"
            AssignmentStatus.GRACE -> {
                val graceEnd = dueAt + (24 * 60 * 60 * 1000L)
                val diff = (graceEnd - now).coerceAtLeast(0L)
                val hours = diff / (3600 * 1000)
                val mins = (diff % (3600 * 1000)) / (60 * 1000)
                val secs = (diff % (60 * 1000)) / 1000
                String.format("GRACE: %02d:%02d:%02d", hours, mins, secs)
            }
            AssignmentStatus.ACTIVE,
            AssignmentStatus.UPCOMING -> {
                val diff = (dueAt - now).coerceAtLeast(0L)
                val hours = diff / (3600 * 1000)
                val mins = (diff % (3600 * 1000)) / (60 * 1000)
                val secs = (diff % (60 * 1000)) / 1000
                String.format("COUNTDOWN: %02d:%02d:%02d", hours, mins, secs)
            }
        }
    }

    fun toggleChecklistItem(itemId: String) {
        _uiState.update { state ->
            val set = state.checkedItemIds.toMutableSet()
            if (set.contains(itemId)) set.remove(itemId) else set.add(itemId)
            state.copy(checkedItemIds = set)
        }
    }

    fun submitCompletion() {
        val assignment = _uiState.value.assignment ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val user = observeCurrentUserUseCase().firstOrNull()
            if (user == null) {
                _uiState.update { it.copy(isSubmitting = false, toastMessage = "User not logged in") }
                return@launch
            }

            submitCompletionUseCase(
                assignment = assignment,
                userId = user.uid,
                displayName = user.displayName.ifBlank { "Adventurer" },
                avatarColorHex = user.avatarColorHex.ifBlank { "#7E5DFF" },
                checkedItemIds = _uiState.value.checkedItemIds.toList()
            ).fold(
                onSuccess = { comp ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            currentUserCompletion = comp,
                            toastMessage = if (assignment.requiresVerification)
                                "Submitted for moderator verification!"
                            else
                                "Quest Completed! Points and XP awarded."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            toastMessage = error.localizedMessage ?: "Failed to submit completion"
                        )
                    }
                }
            )
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
