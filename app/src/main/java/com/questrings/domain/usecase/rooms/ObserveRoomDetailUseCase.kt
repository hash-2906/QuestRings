package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class RoomDetailState(
    val room: Room? = null,
    val members: List<RoomMember> = emptyList(),
    val currentUserRole: RoomRole = RoomRole.MEMBER,
    val pendingRequests: List<JoinRequest> = emptyList()
)

class ObserveRoomDetailUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    operator fun invoke(roomId: String, currentUserId: String): Flow<RoomDetailState> {
        return combine(
            roomRepository.observeRoomById(roomId),
            roomRepository.observeRoomMembers(roomId),
            roomRepository.observeJoinRequests(roomId)
        ) { room, members, requests ->
            val userRole = members.find { it.userId == currentUserId }?.role ?: RoomRole.MEMBER
            RoomDetailState(
                room = room,
                members = members,
                currentUserRole = userRole,
                pendingRequests = requests
            )
        }
    }
}
