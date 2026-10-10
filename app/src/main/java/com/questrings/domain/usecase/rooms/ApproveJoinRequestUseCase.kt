package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.repository.RoomRepository
import javax.inject.Inject

class ApproveJoinRequestUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(request: JoinRequest): Result<Unit> =
        roomRepository.approveJoinRequest(request)
}
