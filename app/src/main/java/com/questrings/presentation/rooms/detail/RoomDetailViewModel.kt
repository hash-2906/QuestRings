package com.questrings.presentation.rooms.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.User
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import com.questrings.domain.usecase.assignments.CreateAssignmentUseCase
import com.questrings.domain.usecase.assignments.DeleteAssignmentUseCase
import com.questrings.domain.usecase.assignments.ObserveAssignmentsWithStatusUseCase
import com.questrings.domain.usecase.assignments.ObservePendingVerificationsUseCase
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.rooms.ApproveJoinRequestUseCase
import com.questrings.domain.usecase.rooms.DeleteRoomUseCase
import com.questrings.domain.usecase.rooms.GenerateJoinCodeUseCase
import com.questrings.domain.usecase.rooms.LeaveRoomUseCase
import com.questrings.domain.usecase.rooms.ObserveRoomDetailUseCase
import com.questrings.domain.usecase.rooms.RejectJoinRequestUseCase
import com.questrings.domain.usecase.rooms.RemoveMemberUseCase
import com.questrings.domain.usecase.rooms.UpdateMemberRoleUseCase
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
class RoomDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeRoomDetailUseCase: ObserveRoomDetailUseCase,
    private val updateMemberRoleUseCase: UpdateMemberRoleUseCase,
    private val removeMemberUseCase: RemoveMemberUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val approveJoinRequestUseCase: ApproveJoinRequestUseCase,
    private val rejectJoinRequestUseCase: RejectJoinRequestUseCase,
    private val generateJoinCodeUseCase: GenerateJoinCodeUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val roomRepository: RoomRepository,
    private val observeAssignmentsWithStatusUseCase: ObserveAssignmentsWithStatusUseCase,
    private val createAssignmentUseCase: CreateAssignmentUseCase,
    private val deleteAssignmentUseCase: DeleteAssignmentUseCase,
    private val observePendingVerificationsUseCase: ObservePendingVerificationsUseCase
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])

    private val _uiState = MutableStateFlow(RoomDetailUiState())
    val uiState: StateFlow<RoomDetailUiState> = _uiState.asStateFlow()

    private val _navigationEffect = MutableSharedFlow<RoomDetailNavigationEffect>()
    val navigationEffect: SharedFlow<RoomDetailNavigationEffect> = _navigationEffect.asSharedFlow()

    init {
        loadRoomData()
    }

    private fun loadRoomData() {
        viewModelScope.launch {
            val user = observeCurrentUserUseCase().firstOrNull()
            val userId = user?.uid ?: ""
            _uiState.update { it.copy(currentUserId = userId) }

            // Observe room, members, requests
            launch {
                observeRoomDetailUseCase(roomId, userId).collect { detailState ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            room = detailState.room,
                            members = detailState.members,
                            currentUserRole = detailState.currentUserRole,
                            pendingRequests = detailState.pendingRequests
                        )
                    }
                }
            }

            // Observe assignments
            launch {
                observeAssignmentsWithStatusUseCase(roomId, userId).collect { assignments ->
                    _uiState.update { it.copy(assignments = assignments) }
                }
            }

            // Observe pending verifications
            launch {
                observePendingVerificationsUseCase(roomId).collect { verifications ->
                    _uiState.update { it.copy(pendingVerifications = verifications) }
                }
            }
        }
    }

    fun selectTab(tab: RoomDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setSettingsOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isSettingsOpen = isOpen) }
    }

    fun setCreateAssignmentSheetOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isCreateAssignmentSheetOpen = isOpen) }
    }

    fun toggleClosedAssignmentsCollapsed() {
        _uiState.update { it.copy(isClosedAssignmentsCollapsed = !it.isClosedAssignmentsCollapsed) }
    }

    fun openAssignment(assignmentId: String) {
        viewModelScope.launch {
            _navigationEffect.emit(
                RoomDetailNavigationEffect.NavigateToAssignmentDetail(roomId, assignmentId)
            )
        }
    }

    fun createAssignment(
        title: String,
        description: String,
        type: AssignmentType,
        items: List<String>,
        dueAt: Long
    ) {
        viewModelScope.launch {
            val userId = _uiState.value.currentUserId
            val isPrivate = _uiState.value.room?.privacy == RoomPrivacy.PRIVATE

            createAssignmentUseCase(
                roomId = roomId,
                createdBy = userId,
                title = title,
                description = description,
                type = type,
                checklistItems = items,
                dueAt = dueAt,
                requiresVerification = isPrivate
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isCreateAssignmentSheetOpen = false,
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

    fun updateMemberRole(targetUserId: String, newRole: RoomRole) {
        viewModelScope.launch {
            updateMemberRoleUseCase(roomId, targetUserId, newRole).fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Member role updated to ${newRole.name}") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to update role") }
                }
            )
        }
    }

    fun removeMember(targetUserId: String) {
        viewModelScope.launch {
            removeMemberUseCase(roomId, targetUserId).fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Member removed from room") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to remove member") }
                }
            )
        }
    }

    fun leaveRoom() {
        val userId = _uiState.value.currentUserId
        if (userId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLeavingOrDeleting = true) }
            leaveRoomUseCase(roomId, userId).fold(
                onSuccess = {
                    _navigationEffect.emit(RoomDetailNavigationEffect.NavigateBack)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLeavingOrDeleting = false,
                            toastMessage = error.localizedMessage ?: "Failed to leave room"
                        )
                    }
                }
            )
        }
    }

    fun approveRequest(request: JoinRequest) {
        viewModelScope.launch {
            approveJoinRequestUseCase(request).fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Approved ${request.displayName}") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to approve request") }
                }
            )
        }
    }

    fun rejectRequest(request: JoinRequest) {
        viewModelScope.launch {
            rejectJoinRequestUseCase(request.id, roomId).fold(
                onSuccess = {
                    _uiState.update { it.copy(toastMessage = "Rejected ${request.displayName}") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to reject request") }
                }
            )
        }
    }

    fun saveSettings(name: String, description: String, privacy: RoomPrivacy) {
        val currentRoom = _uiState.value.room ?: return
        viewModelScope.launch {
            val updated = currentRoom.copy(
                name = name,
                description = description,
                privacy = privacy
            )
            roomRepository.updateRoom(updated).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            room = updated,
                            isSettingsOpen = false,
                            toastMessage = "Room settings updated"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to update room") }
                }
            )
        }
    }

    fun regenerateCode() {
        val currentRoom = _uiState.value.room ?: return
        viewModelScope.launch {
            val newCode = generateJoinCodeUseCase()
            val updated = currentRoom.copy(joinCode = newCode)
            roomRepository.updateRoom(updated).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            room = updated,
                            toastMessage = "New Join Code generated: $newCode"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(toastMessage = error.localizedMessage ?: "Failed to regenerate code") }
                }
            )
        }
    }

    fun deleteRoom() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLeavingOrDeleting = true) }
            deleteRoomUseCase(roomId).fold(
                onSuccess = {
                    _navigationEffect.emit(RoomDetailNavigationEffect.NavigateBack)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLeavingOrDeleting = false,
                            toastMessage = error.localizedMessage ?: "Failed to delete room"
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
