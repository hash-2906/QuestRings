package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LeaveRoomUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(roomId: String, userId: String): Result<Unit> = runCatching {
        val members = roomRepository.observeRoomMembers(roomId).firstOrNull() ?: emptyList()
        val userMember = members.find { it.userId == userId }
            ?: throw IllegalStateException("User not a member")

        if (userMember.role == RoomRole.ADMIN) {
            val adminCount = members.count { it.role == RoomRole.ADMIN }
            if (adminCount <= 1) {
                throw IllegalStateException("You are the sole Admin. Please transfer Admin ownership or delete the room.")
            }
        }

        roomRepository.leaveRoom(roomId, userId).getOrThrow()
    }
}
