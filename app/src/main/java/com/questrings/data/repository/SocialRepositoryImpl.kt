package com.questrings.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.social.FeedEventType
import com.questrings.domain.model.social.FriendRequest
import com.questrings.domain.model.social.FriendshipStatus
import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.model.social.SocialFeedEvent
import com.questrings.domain.repository.SocialRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SocialRepository {

    override fun observeFriends(userId: String): Flow<List<PublicProfile>> = callbackFlow {
        val registration = firestore.collection("users")
            .document(userId)
            .collection("friends")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val friendIds = snapshot.documents.mapNotNull { it.getString("friendId") ?: it.id }
                if (friendIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Fetch details for friends (chunk by 10 for whereIn limits)
                firestore.collection("users")
                    .whereIn("uid", friendIds.take(10))
                    .get()
                    .addOnSuccessListener { usersSnap ->
                        val profiles = usersSnap.documents.mapNotNull { doc ->
                            val uid = doc.getString("uid") ?: doc.id
                            val name = doc.getString("displayName") ?: "Adventurer"
                            val color = doc.getString("avatarColorHex") ?: "#7E5DFF"
                            val xp = doc.getLong("xp")?.toInt() ?: 0
                            val level = doc.getLong("level")?.toInt() ?: 1
                            val streak = doc.getLong("streak")?.toInt() ?: 0
                            val medal = MedalProgressionConfig.evaluate(xp, level)

                            PublicProfile(
                                userId = uid,
                                displayName = name,
                                avatarColorHex = color,
                                level = level,
                                currentMedal = medal,
                                currentStreak = streak,
                                totalXp = xp,
                                friendshipStatus = FriendshipStatus.FRIENDS
                            )
                        }
                        trySend(profiles)
                    }
                    .addOnFailureListener {
                        trySend(emptyList())
                    }
            }
        awaitClose { registration.remove() }
    }

    override fun observeFriendRequests(userId: String): Flow<List<FriendRequest>> = callbackFlow {
        val registration = firestore.collection("users")
            .document(userId)
            .collection("friendRequests")
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val requests = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val fromUserId = doc.getString("fromUserId") ?: return@mapNotNull null
                    val fromDisplayName = doc.getString("fromDisplayName") ?: "Adventurer"
                    val fromAvatarColorHex = doc.getString("fromAvatarColorHex") ?: "#7E5DFF"
                    val sentAt = doc.getLong("sentAt") ?: System.currentTimeMillis()

                    FriendRequest(
                        id = id,
                        fromUserId = fromUserId,
                        fromDisplayName = fromDisplayName,
                        fromAvatarColorHex = fromAvatarColorHex,
                        sentAt = sentAt
                    )
                }
                trySend(requests)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Result<Unit> = runCatching {
        val senderDoc = firestore.collection("users").document(fromUserId).get().await()
        val senderName = senderDoc.getString("displayName") ?: "Adventurer"
        val senderColor = senderDoc.getString("avatarColorHex") ?: "#7E5DFF"

        val reqData = mapOf(
            "id" to fromUserId,
            "fromUserId" to fromUserId,
            "fromDisplayName" to senderName,
            "fromAvatarColorHex" to senderColor,
            "sentAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(toUserId)
            .collection("friendRequests")
            .document(fromUserId)
            .set(reqData, SetOptions.merge())
            .await()
    }

    override suspend fun acceptFriendRequest(
        requestId: String,
        fromUserId: String,
        toUserId: String
    ): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()

        val batch = firestore.batch()

        val toFriendRef = firestore.collection("users").document(toUserId).collection("friends").document(fromUserId)
        batch.set(toFriendRef, mapOf("friendId" to fromUserId, "since" to now))

        val fromFriendRef = firestore.collection("users").document(fromUserId).collection("friends").document(toUserId)
        batch.set(fromFriendRef, mapOf("friendId" to toUserId, "since" to now))

        val reqRef = firestore.collection("users").document(toUserId).collection("friendRequests").document(requestId)
        batch.delete(reqRef)

        batch.commit().await()
    }

    override suspend fun rejectFriendRequest(requestId: String, userId: String): Result<Unit> = runCatching {
        firestore.collection("users")
            .document(userId)
            .collection("friendRequests")
            .document(requestId)
            .delete()
            .await()
    }

    override suspend fun removeFriend(userId: String, friendId: String): Result<Unit> = runCatching {
        val batch = firestore.batch()
        batch.delete(firestore.collection("users").document(userId).collection("friends").document(friendId))
        batch.delete(firestore.collection("users").document(friendId).collection("friends").document(userId))
        batch.commit().await()
    }

    override suspend fun searchUsers(query: String, currentUserId: String): Result<List<PublicProfile>> = runCatching {
        if (query.isBlank()) return@runCatching emptyList()

        val snap = firestore.collection("users")
            .orderBy("displayName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limit(15)
            .get()
            .await()

        snap.documents.filter { it.id != currentUserId }.mapNotNull { doc ->
            val uid = doc.getString("uid") ?: doc.id
            val name = doc.getString("displayName") ?: "Adventurer"
            val color = doc.getString("avatarColorHex") ?: "#7E5DFF"
            val xp = doc.getLong("xp")?.toInt() ?: 0
            val level = doc.getLong("level")?.toInt() ?: 1
            val streak = doc.getLong("streak")?.toInt() ?: 0
            val medal = MedalProgressionConfig.evaluate(xp, level)

            PublicProfile(
                userId = uid,
                displayName = name,
                avatarColorHex = color,
                level = level,
                currentMedal = medal,
                currentStreak = streak,
                totalXp = xp,
                friendshipStatus = FriendshipStatus.NONE
            )
        }
    }

    override fun observeActivityFeed(
        userId: String,
        friendIds: List<String>
    ): Flow<List<SocialFeedEvent>> = callbackFlow {
        val allowedUids = (friendIds + userId).distinct().take(30)

        val query = if (allowedUids.isNotEmpty()) {
            firestore.collection("feed_events")
                .whereIn("userId", allowedUids)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
        } else {
            firestore.collection("feed_events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
        }

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val events = snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val uid = doc.getString("userId") ?: return@mapNotNull null
                val name = doc.getString("displayName") ?: "Adventurer"
                val color = doc.getString("avatarColorHex") ?: "#7E5DFF"
                val typeStr = doc.getString("eventType") ?: return@mapNotNull null
                val eventType = runCatching { FeedEventType.valueOf(typeStr) }.getOrDefault(FeedEventType.MEDAL_UNLOCKED)
                val desc = doc.getString("description") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val xpEarned = doc.getLong("xpEarned")?.toInt() ?: 0

                val rawReactions = doc.get("reactions") as? Map<*, *> ?: emptyMap<Any, Any>()
                val reactions = rawReactions.entries.associate { (k, v) ->
                    val emoji = k.toString()
                    val userList = (v as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    emoji to userList
                }

                SocialFeedEvent(
                    id = id,
                    userId = uid,
                    displayName = name,
                    avatarColorHex = color,
                    eventType = eventType,
                    description = desc,
                    timestamp = timestamp,
                    xpEarned = xpEarned,
                    reactions = reactions
                )
            }
            trySend(events)
        }
        awaitClose { registration.remove() }
    }

    override suspend fun addReaction(
        eventId: String,
        emoji: String,
        userId: String
    ): Result<Unit> = runCatching {
        firestore.collection("feed_events")
            .document(eventId)
            .update("reactions.$emoji", FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun removeReaction(
        eventId: String,
        emoji: String,
        userId: String
    ): Result<Unit> = runCatching {
        firestore.collection("feed_events")
            .document(eventId)
            .update("reactions.$emoji", FieldValue.arrayRemove(userId))
            .await()
    }

    override suspend fun publishFeedEvent(event: SocialFeedEvent): Result<Unit> = runCatching {
        val map = mapOf(
            "id" to event.id,
            "userId" to event.userId,
            "displayName" to event.displayName,
            "avatarColorHex" to event.avatarColorHex,
            "eventType" to event.eventType.name,
            "description" to event.description,
            "timestamp" to event.timestamp,
            "xpEarned" to event.xpEarned,
            "reactions" to event.reactions
        )
        firestore.collection("feed_events")
            .document(event.id)
            .set(map)
            .await()
    }
}
