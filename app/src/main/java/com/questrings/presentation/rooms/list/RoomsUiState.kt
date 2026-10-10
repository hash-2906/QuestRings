package com.questrings.presentation.rooms.list

import com.questrings.domain.model.room.Room

enum class RoomsTab { MY_ROOMS, JOINED_ROOMS }

data class RoomsUiState(
    val myRooms: List<Room> = emptyList(),
    val joinedRooms: List<Room> = emptyList(),
    val selectedTab: RoomsTab = RoomsTab.MY_ROOMS,
    val isLoading: Boolean = true,
    val isCreateRoomSheetOpen: Boolean = false,
    val toastMessage: String? = null
)

sealed interface RoomsUiEvent {
    data class SelectTab(val tab: RoomsTab) : RoomsUiEvent
    data object OpenCreateRoomSheet : RoomsUiEvent
    data object CloseCreateRoomSheet : RoomsUiEvent
    data object DismissToast : RoomsUiEvent
}
