package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateMemberRoleUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(
        roomId: String,
        targetUserId: String,
        newRole: RoomRole,
        actorUserId: String
    ): Result<Unit> = runCatching {
        val members = roomRepository.observeRoomMembers(roomId).firstOrNull() ?: emptyList()
        val actor = members.find { it.userId == actorUserId }
            ?: throw IllegalStateException("Actor not in room")

        if (actor.role != RoomRole.ADMIN) {
            throw IllegalStateException("Only Admin can assign or alter roles")
        }

        // Cannot demote self if only 1 admin left
        if (targetUserId == actorUserId && newRole != RoomRole.ADMIN) {
            val adminCount = members.count { it.role == RoomRole.ADMIN }
            if (adminCount <= 1) {
                throw IllegalStateException("Cannot demote the sole room Admin")
            }
        }

        roomRepository.updateMemberRole(roomId, targetUserId, newRole).getOrThrow()
    }
}
