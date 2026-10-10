package com.questrings.domain.usecase.rooms

import com.questrings.domain.repository.RoomRepository
import javax.inject.Inject

class RejectJoinRequestUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(requestId: String, roomId: String): Result<Unit> =
        roomRepository.rejectJoinRequest(requestId, roomId)
}
