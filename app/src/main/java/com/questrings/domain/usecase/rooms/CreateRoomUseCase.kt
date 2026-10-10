package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.repository.RoomRepository
import java.util.UUID
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val roomRepository: RoomRepository,
    private val generateJoinCodeUseCase: GenerateJoinCodeUseCase
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        emoji: String,
        privacy: RoomPrivacy,
        creatorId: String,
        maxMembers: Int
    ): Result<Room> = runCatching {
        val joinCode = generateJoinCodeUseCase()
        val roomId = UUID.randomUUID().toString()

        val room = Room(
            id = roomId,
            name = name,
            description = description,
            emoji = emoji,
            privacy = privacy,
            joinCode = joinCode,
            creatorId = creatorId,
            maxMembers = maxMembers.coerceIn(5, 200),
            memberCount = 1,
            activeAssignmentCount = 0,
            createdAt = System.currentTimeMillis(),
            isActive = true
        )

        roomRepository.createRoom(room).getOrThrow()
    }
}
