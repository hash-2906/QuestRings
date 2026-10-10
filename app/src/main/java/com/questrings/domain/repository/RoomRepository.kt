package com.questrings.domain.repository

import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomRole
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun observeMyRooms(userId: String): Flow<List<Room>>
    fun observeJoinedRooms(userId: String): Flow<List<Room>>
    fun observeRoomById(roomId: String): Flow<Room?>
    fun observeRoomMembers(roomId: String): Flow<List<RoomMember>>
    fun observeJoinRequests(roomId: String): Flow<List<JoinRequest>>
    suspend fun createRoom(room: Room): Result<Room>
    suspend fun updateRoom(room: Room): Result<Unit>
    suspend fun deleteRoom(roomId: String): Result<Unit>
    suspend fun getRoomByJoinCode(code: String): Result<Room?>
    suspend fun joinRoomPublic(roomId: String, member: RoomMember): Result<Unit>
    suspend fun submitJoinRequest(request: JoinRequest): Result<Unit>
    suspend fun approveJoinRequest(request: JoinRequest): Result<Unit>
    suspend fun rejectJoinRequest(requestId: String, roomId: String): Result<Unit>
    suspend fun updateMemberRole(
        roomId: String, userId: String, newRole: RoomRole
    ): Result<Unit>
    suspend fun removeMember(roomId: String, userId: String): Result<Unit>
    suspend fun leaveRoom(roomId: String, userId: String): Result<Unit>
}
