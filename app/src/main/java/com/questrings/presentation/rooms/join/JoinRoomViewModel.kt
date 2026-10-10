package com.questrings.presentation.rooms.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.User
import com.questrings.domain.repository.RoomRepository
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.rooms.JoinRoomByCodeUseCase
import com.questrings.domain.usecase.rooms.JoinRoomResult
import dagger.hilt.android.lifecycle.HiltViewModel
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
class JoinRoomViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val roomRepository: RoomRepository,
    private val joinRoomByCodeUseCase: JoinRoomByCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinRoomUiState())
    val uiState: StateFlow<JoinRoomUiState> = _uiState.asStateFlow()

    private val _navigationEffect = MutableSharedFlow<JoinRoomNavigationEffect>()
    val navigationEffect: SharedFlow<JoinRoomNavigationEffect> = _navigationEffect.asSharedFlow()

    fun onCodeChange(newCode: String) {
        val uppercase = newCode.uppercase().take(6)
        _uiState.update {
            it.copy(
                code = uppercase,
                errorMessage = null,
                previewRoom = if (uppercase.length < 6) null else it.previewRoom,
                joinStatus = if (uppercase.length < 6) null else it.joinStatus
            )
        }
        if (uppercase.length == 6) {
            lookupRoom(uppercase)
        }
    }

    fun lookupRoom(code: String = _uiState.value.code) {
        if (code.length != 6) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLookingUp = true, errorMessage = null) }
            val result = roomRepository.getRoomByJoinCode(code)
            result.fold(
                onSuccess = { room ->
                    if (room != null) {
                        _uiState.update {
                            it.copy(
                                isLookingUp = false,
                                previewRoom = room,
                                errorMessage = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLookingUp = false,
                                previewRoom = null,
                                errorMessage = "No active room found with code '$code'."
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLookingUp = false,
                            errorMessage = error.localizedMessage ?: "Failed to lookup room."
                        )
                    }
                }
            )
        }
    }

    fun joinRoom() {
        val code = _uiState.value.code
        if (code.length != 6) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentUser: User? = observeCurrentUserUseCase().firstOrNull()
            if (currentUser == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "User authentication required.")
                }
                return@launch
            }

            val joinResult = joinRoomByCodeUseCase(
                code = code,
                userId = currentUser.uid,
                displayName = currentUser.displayName.ifBlank { "Adventurer" },
                avatarColorHex = currentUser.avatarColorHex.ifBlank { "#7E5DFF" }
            )

            joinResult.fold(
                onSuccess = { result ->
                    when (result) {
                        is JoinRoomResult.Joined -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    joinStatus = JoinStatusType.JOINED
                                )
                            }
                            _navigationEffect.emit(JoinRoomNavigationEffect.NavigateToRoom(result.room.id))
                        }
                        is JoinRoomResult.Requested -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    joinStatus = JoinStatusType.REQUEST_SUBMITTED
                                )
                            }
                        }
                        is JoinRoomResult.AlreadyMember -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    joinStatus = JoinStatusType.ALREADY_MEMBER
                                )
                            }
                            _navigationEffect.emit(JoinRoomNavigationEffect.NavigateToRoom(result.room.id))
                        }
                        is JoinRoomResult.RoomFull -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    joinStatus = JoinStatusType.ROOM_FULL,
                                    errorMessage = "This room has reached maximum capacity."
                                )
                            }
                        }
                        is JoinRoomResult.NotFound -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    joinStatus = JoinStatusType.NOT_FOUND,
                                    errorMessage = "Invalid or expired room join code."
                                )
                            }
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to join room."
                        )
                    }
                }
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
