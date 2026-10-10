package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.RoomPermissions
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class RemoveMemberUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(
        roomId: String,
        targetUserId: String,
        actorUserId: String
    ): Result<Unit> = runCatching {
        val members = roomRepository.observeRoomMembers(roomId).firstOrNull() ?: emptyList()
        val actor = members.find { it.userId == actorUserId }
            ?: throw IllegalStateException("Actor not in room")
        val target = members.find { it.userId == targetUserId }
            ?: throw IllegalStateException("Target not in room")

        if (!RoomPermissions.canRemoveMember(actor.role, target.role)) {
            throw IllegalStateException("Insufficient permissions to remove member")
        }

        roomRepository.removeMember(roomId, targetUserId).getOrThrow()
    }
}
