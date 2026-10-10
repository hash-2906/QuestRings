package com.questrings.presentation.assignment.leaderboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.repository.AssignmentRepository
import com.questrings.domain.usecase.assignments.ObserveRoomLeaderboardUseCase
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomLeaderboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeRoomLeaderboardUseCase: ObserveRoomLeaderboardUseCase,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])

    private val _uiState = MutableStateFlow(RoomLeaderboardUiState())
    val uiState: StateFlow<RoomLeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            val user = observeCurrentUserUseCase().firstOrNull()
            val userId = user?.uid ?: ""

            launch {
                observeRoomLeaderboardUseCase(roomId, userId).collect { list ->
                    val userEntry = list.find { it.userId == userId }
                    _uiState.update {
                        it.copy(
                            entries = list,
                            currentUserEntry = userEntry,
                            totalCompetitors = list.count { e -> e.totalPoints > 0 },
                            isLoading = false
                        )
                    }
                }
            }

            launch {
                assignmentRepository.observeAssignmentsForRoom(roomId).collect { assignments ->
                    _uiState.update { it.copy(totalAssignmentsPosted = assignments.size) }
                }
            }
        }
    }
}
