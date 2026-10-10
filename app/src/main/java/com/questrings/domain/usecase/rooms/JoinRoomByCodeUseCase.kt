package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.JoinRequestStatus
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import java.util.UUID
import javax.inject.Inject

sealed interface JoinRoomResult {
    data class Joined(val room: Room) : JoinRoomResult
    data class Requested(val room: Room) : JoinRoomResult
    data class AlreadyMember(val room: Room) : JoinRoomResult
    data class RoomFull(val room: Room) : JoinRoomResult
    data object NotFound : JoinRoomResult
}

class JoinRoomByCodeUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(
        code: String,
        userId: String,
        displayName: String,
        avatarColorHex: String
    ): Result<JoinRoomResult> = runCatching {
        val room = roomRepository.getRoomByJoinCode(code).getOrNull()
            ?: return@runCatching JoinRoomResult.NotFound

        // Check capacity
        if (room.memberCount >= room.maxMembers) {
            return@runCatching JoinRoomResult.RoomFull(room)
        }

        if (room.privacy == RoomPrivacy.PUBLIC) {
            val newMember = RoomMember(
                userId = userId,
                roomId = room.id,
                displayName = displayName,
                avatarColorHex = avatarColorHex,
                role = RoomRole.MEMBER,
                joinedAt = System.currentTimeMillis()
            )
            roomRepository.joinRoomPublic(room.id, newMember).getOrThrow()
            JoinRoomResult.Joined(room)
        } else {
            val request = JoinRequest(
                id = UUID.randomUUID().toString(),
                roomId = room.id,
                userId = userId,
                displayName = displayName,
                avatarColorHex = avatarColorHex,
                requestedAt = System.currentTimeMillis(),
                status = JoinRequestStatus.PENDING
            )
            roomRepository.submitJoinRequest(request).getOrThrow()
            JoinRoomResult.Requested(room)
        }
    }
}
