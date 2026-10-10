package com.questrings.presentation.rooms.join

import com.questrings.domain.model.room.Room

enum class JoinStatusType {
    JOINED,
    REQUEST_SUBMITTED,
    ALREADY_MEMBER,
    ROOM_FULL,
    NOT_FOUND
}

sealed interface JoinRoomNavigationEffect {
    data class NavigateToRoom(val roomId: String) : JoinRoomNavigationEffect
    data object NavigateBack : JoinRoomNavigationEffect
}

data class JoinRoomUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val isLookingUp: Boolean = false,
    val previewRoom: Room? = null,
    val errorMessage: String? = null,
    val joinStatus: JoinStatusType? = null
)
