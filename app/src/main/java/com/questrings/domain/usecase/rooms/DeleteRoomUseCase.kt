package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteRoomUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(roomId: String, actorUserId: String): Result<Unit> = runCatching {
        val members = roomRepository.observeRoomMembers(roomId).firstOrNull() ?: emptyList()
        val actor = members.find { it.userId == actorUserId }
            ?: throw IllegalStateException("Actor not in room")

        if (actor.role != RoomRole.ADMIN) {
            throw IllegalStateException("Only the room Admin can delete this room")
        }

        roomRepository.deleteRoom(roomId).getOrThrow()
    }
}
