package com.questrings.presentation.profile.public

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.social.FriendshipStatus
import com.questrings.domain.repository.AuthRepository
import com.questrings.domain.usecase.social.GetPublicProfileUseCase
import com.questrings.domain.usecase.social.ObserveFriendGraphUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val getPublicProfileUseCase: GetPublicProfileUseCase,
    private val observeFriendGraphUseCase: ObserveFriendGraphUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val targetUserId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUserId = authRepository.currentUser.firstOrNull()?.uid ?: ""
            val result = getPublicProfileUseCase(targetUserId, currentUserId)
            _uiState.update {
                it.copy(
                    profile = result.getOrNull(),
                    isLoading = false
                )
            }
        }
    }

    fun sendFriendRequest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val currentUserId = authRepository.currentUser.firstOrNull()?.uid ?: ""
            observeFriendGraphUseCase.sendRequest(currentUserId, targetUserId)
            _uiState.update {
                it.copy(
                    isActionInProgress = false,
                    profile = it.profile?.copy(friendshipStatus = FriendshipStatus.PENDING_SENT)
                )
            }
        }
    }

    fun removeFriend() {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            val currentUserId = authRepository.currentUser.firstOrNull()?.uid ?: ""
            observeFriendGraphUseCase.removeFriend(currentUserId, targetUserId)
            _uiState.update {
                it.copy(
                    isActionInProgress = false,
                    profile = it.profile?.copy(friendshipStatus = FriendshipStatus.NONE)
                )
            }
        }
    }
}
