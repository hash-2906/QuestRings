package com.questrings.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.JoinRequestStatus
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.model.room.RoomRole
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomRepository {

    override fun observeMyRooms(userId: String): Flow<List<Room>> = callbackFlow {
        val registration: ListenerRegistration = firestore.collection("rooms")
            .whereEqualTo("creatorId", userId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Room::class.java)
                } ?: emptyList()
                trySend(rooms)
            }
        awaitClose { registration.remove() }
    }

    override fun observeJoinedRooms(userId: String): Flow<List<Room>> = callbackFlow {
        // Query group listener across members collectionGroup where userId matches
        val registration = firestore.collectionGroup("members")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val roomIds = snapshot.documents.mapNotNull { it.getString("roomId") }.toSet()
                if (roomIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Fetch room docs
                firestore.collection("rooms")
                    .whereIn("id", roomIds.toList().take(10))
                    .whereEqualTo("isActive", true)
                    .get()
                    .addOnSuccessListener { rSnap ->
                        val rooms = rSnap.documents.mapNotNull { it.toObject(Room::class.java) }
                            .filter { it.creatorId != userId } // Joined rooms only
                        trySend(rooms)
                    }
            }
        awaitClose { registration.remove() }
    }

    override fun observeRoomById(roomId: String): Flow<Room?> = callbackFlow {
        val registration = firestore.collection("rooms").document(roomId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(Room::class.java))
            }
        awaitClose { registration.remove() }
    }

    override fun observeRoomMembers(roomId: String): Flow<List<RoomMember>> = callbackFlow {
        val registration = firestore.collection("rooms").document(roomId)
            .collection("members")
            .orderBy("totalXpEarned", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val members = snapshot?.documents?.mapNotNull { it.toObject(RoomMember::class.java) } ?: emptyList()
                trySend(members)
            }
        awaitClose { registration.remove() }
    }

    override fun observeJoinRequests(roomId: String): Flow<List<JoinRequest>> = callbackFlow {
        val registration = firestore.collection("rooms").document(roomId)
            .collection("joinRequests")
            .whereEqualTo("status", JoinRequestStatus.PENDING.name)
            .addSnapshotListener { snapshot, _ ->
                val requests = snapshot?.documents?.mapNotNull { it.toObject(JoinRequest::class.java) } ?: emptyList()
                trySend(requests)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun createRoom(room: Room): Result<Room> = runCatching {
        val batch = firestore.batch()

        // 1. Room doc
        val roomRef = firestore.collection("rooms").document(room.id)
        batch.set(roomRef, room)

        // 2. Global code doc
        val codeRef = firestore.collection("roomCodes").document(room.joinCode)
        batch.set(codeRef, mapOf("roomId" to room.id))

        // 3. Creator Admin member record
        val memberRef = roomRef.collection("members").document(room.creatorId)
        val adminMember = RoomMember(
            userId = room.creatorId,
            roomId = room.id,
            displayName = "Creator",
            role = RoomRole.ADMIN,
            joinedAt = room.createdAt
        )
        batch.set(memberRef, adminMember)

        batch.commit().await()
        room
    }

    override suspend fun updateRoom(room: Room): Result<Unit> = runCatching {
        firestore.collection("rooms").document(room.id).set(room).await()
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> = runCatching {
        val roomDoc = firestore.collection("rooms").document(roomId).get().await()
        val joinCode = roomDoc.getString("joinCode")

        val batch = firestore.batch()
        batch.delete(firestore.collection("rooms").document(roomId))
        if (!joinCode.isNullOrBlank()) {
            batch.delete(firestore.collection("roomCodes").document(joinCode))
        }
        batch.commit().await()
    }

    override suspend fun getRoomByJoinCode(code: String): Result<Room?> = runCatching {
        val codeDoc = firestore.collection("roomCodes").document(code.uppercase().trim()).get().await()
        if (!codeDoc.exists()) return@runCatching null
        val roomId = codeDoc.getString("roomId") ?: return@runCatching null
        val roomDoc = firestore.collection("rooms").document(roomId).get().await()
        roomDoc.toObject(Room::class.java)
    }

    override suspend fun joinRoomPublic(roomId: String, member: RoomMember): Result<Unit> = runCatching {
        val batch = firestore.batch()
        val memberRef = firestore.collection("rooms").document(roomId).collection("members").document(member.userId)
        batch.set(memberRef, member)

        val roomRef = firestore.collection("rooms").document(roomId)
        batch.update(roomRef, "memberCount", FieldValue.increment(1))
        batch.commit().await()
    }

    override suspend fun submitJoinRequest(request: JoinRequest): Result<Unit> = runCatching {
        firestore.collection("rooms").document(request.roomId)
            .collection("joinRequests").document(request.id)
            .set(request).await()
    }

    override suspend fun approveJoinRequest(request: JoinRequest): Result<Unit> = runCatching {
        val batch = firestore.batch()

        // 1. Mark request approved
        val reqRef = firestore.collection("rooms").document(request.roomId)
            .collection("joinRequests").document(request.id)
        batch.update(reqRef, "status", JoinRequestStatus.APPROVED.name)

        // 2. Add member record
        val memberRef = firestore.collection("rooms").document(request.roomId)
            .collection("members").document(request.userId)
        val newMember = RoomMember(
            userId = request.userId,
            roomId = request.roomId,
            displayName = request.displayName,
            avatarColorHex = request.avatarColorHex,
            role = RoomRole.MEMBER,
            joinedAt = System.currentTimeMillis()
        )
        batch.set(memberRef, newMember)

        // 3. Increment room memberCount
        val roomRef = firestore.collection("rooms").document(request.roomId)
        batch.update(roomRef, "memberCount", FieldValue.increment(1))

        batch.commit().await()
    }

    override suspend fun rejectJoinRequest(requestId: String, roomId: String): Result<Unit> = runCatching {
        firestore.collection("rooms").document(roomId)
            .collection("joinRequests").document(requestId)
            .update("status", JoinRequestStatus.REJECTED.name).await()
    }

    override suspend fun updateMemberRole(
        roomId: String,
        userId: String,
        newRole: RoomRole
    ): Result<Unit> = runCatching {
        firestore.collection("rooms").document(roomId)
            .collection("members").document(userId)
            .update("role", newRole.name).await()
    }

    override suspend fun removeMember(roomId: String, userId: String): Result<Unit> = runCatching {
        val batch = firestore.batch()
        batch.delete(
            firestore.collection("rooms").document(roomId)
                .collection("members").document(userId)
        )
        batch.update(
            firestore.collection("rooms").document(roomId),
            "memberCount",
            FieldValue.increment(-1)
        )
        batch.commit().await()
    }

    override suspend fun leaveRoom(roomId: String, userId: String): Result<Unit> = removeMember(roomId, userId)
}
