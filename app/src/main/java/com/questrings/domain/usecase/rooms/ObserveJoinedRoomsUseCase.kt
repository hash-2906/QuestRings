package com.questrings.domain.usecase.rooms

import com.questrings.domain.model.room.Room
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveJoinedRoomsUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    operator fun invoke(userId: String): Flow<List<Room>> = roomRepository.observeJoinedRooms(userId)
}
