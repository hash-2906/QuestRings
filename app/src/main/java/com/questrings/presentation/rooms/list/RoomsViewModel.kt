package com.questrings.presentation.rooms.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.domain.usecase.rooms.CreateRoomUseCase
import com.questrings.domain.usecase.rooms.ObserveJoinedRoomsUseCase
import com.questrings.domain.usecase.rooms.ObserveMyRoomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RoomsNavigationEffect {
    data class NavigateToRoom(val roomId: String) : RoomsNavigationEffect
    data class NavigateToShare(val roomId: String) : RoomsNavigationEffect
    data object NavigateToJoin : RoomsNavigationEffect
}

@HiltViewModel
class RoomsViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeMyRoomsUseCase: ObserveMyRoomsUseCase,
    private val observeJoinedRoomsUseCase: ObserveJoinedRoomsUseCase,
    private val createRoomUseCase: CreateRoomUseCase
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(RoomsTab.MY_ROOMS)
    private val _isCreateRoomSheetOpen = MutableStateFlow(false)
    private val _toastMessage = MutableStateFlow<String?>(null)

    private val _navigationEffect = MutableSharedFlow<RoomsNavigationEffect>()
    val navigationEffect: SharedFlow<RoomsNavigationEffect> = _navigationEffect.asSharedFlow()

    private val currentUserFlow = observeCurrentUserUseCase()

    private val myRoomsFlow = currentUserFlow.flatMapLatest { user ->
        if (user != null) observeMyRoomsUseCase(user.uid) else flowOf(emptyList())
    }

    private val joinedRoomsFlow = currentUserFlow.flatMapLatest { user ->
        if (user != null) observeJoinedRoomsUseCase(user.uid) else flowOf(emptyList())
    }

    val uiState: StateFlow<RoomsUiState> = combine(
        myRoomsFlow,
        joinedRoomsFlow,
        _selectedTab,
        _isCreateRoomSheetOpen,
        _toastMessage
    ) { myRooms, joinedRooms, tab, isSheetOpen, toast ->
        RoomsUiState(
            myRooms = myRooms,
            joinedRooms = joinedRooms,
            selectedTab = tab,
            isLoading = false,
            isCreateRoomSheetOpen = isSheetOpen,
            toastMessage = toast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RoomsUiState()
    )

    fun onEvent(event: RoomsUiEvent) {
        when (event) {
            is RoomsUiEvent.SelectTab -> _selectedTab.value = event.tab
            is RoomsUiEvent.OpenCreateRoomSheet -> _isCreateRoomSheetOpen.value = true
            is RoomsUiEvent.CloseCreateRoomSheet -> _isCreateRoomSheetOpen.value = false
            is RoomsUiEvent.DismissToast -> _toastMessage.value = null
        }
    }

    fun createRoom(
        name: String,
        description: String,
        emoji: String,
        privacy: RoomPrivacy,
        maxMembers: Int
    ) {
        val user = currentUserFlow.stateIn(viewModelScope).value ?: return
        viewModelScope.launch {
            createRoomUseCase(
                name = name,
                description = description,
                emoji = emoji,
                privacy = privacy,
                creatorId = user.uid,
                maxMembers = maxMembers
            ).onSuccess { room ->
                _isCreateRoomSheetOpen.value = false
                _navigationEffect.emit(RoomsNavigationEffect.NavigateToShare(room.id))
            }.onFailure { error ->
                _toastMessage.value = error.message ?: "Failed to create room"
            }
        }
    }
}
