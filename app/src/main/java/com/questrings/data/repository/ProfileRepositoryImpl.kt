package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.social.FeedEventType
import com.questrings.domain.model.social.FriendshipStatus
import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.model.social.SocialFeedEvent
import com.questrings.domain.repository.ProfileRepository
import com.questrings.domain.repository.RoomRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val roomRepository: RoomRepository
) : ProfileRepository {

    override suspend fun getPublicProfile(
        targetUserId: String,
        currentUserId: String
    ): Result<PublicProfile> = runCatching {
        val userDoc = firestore.collection("users").document(targetUserId).get().await()
        val name = userDoc.getString("displayName") ?: "Adventurer"
        val color = userDoc.getString("avatarColorHex") ?: "#7E5DFF"
        val xp = userDoc.getLong("xp")?.toInt() ?: 0
        val level = userDoc.getLong("level")?.toInt() ?: 1
        val streak = userDoc.getLong("streak")?.toInt() ?: 0
        val medal = MedalProgressionConfig.evaluate(xp, level)

        // Category breakdown from stats document
        val statsDoc = firestore.collection("users").document(targetUserId)
            .collection("stats").document("current").get().await()
        val rawCatXp = statsDoc.get("xpByCategory") as? Map<*, *> ?: emptyMap<Any, Any>()
        val xpByCategory = HabitCategory.values().associateWith { cat ->
            (rawCatXp[cat.name] as? Number)?.toInt() ?: 0
        }

        // Determine friendship status
        val friendshipStatus = when {
            targetUserId == currentUserId -> FriendshipStatus.SELF
            else -> {
                val friendDoc = firestore.collection("users").document(currentUserId)
                    .collection("friends").document(targetUserId).get().await()
                if (friendDoc.exists()) {
                    FriendshipStatus.FRIENDS
                } else {
                    val sentReq = firestore.collection("users").document(targetUserId)
                        .collection("friendRequests").document(currentUserId).get().await()
                    if (sentReq.exists()) {
                        FriendshipStatus.PENDING_SENT
                    } else {
                        val receivedReq = firestore.collection("users").document(currentUserId)
                            .collection("friendRequests").document(targetUserId).get().await()
                        if (receivedReq.exists()) {
                            FriendshipStatus.PENDING_RECEIVED
                        } else {
                            FriendshipStatus.NONE
                        }
                    }
                }
            }
        }

        // Fetch recent achievements
        val achievementsSnap = try {
            firestore.collection("feed_events")
                .whereEqualTo("userId", targetUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()
        } catch (_: Exception) {
            null
        }

        val recentAchievements = achievementsSnap?.documents?.mapNotNull { doc ->
            val typeStr = doc.getString("eventType") ?: return@mapNotNull null
            val type = runCatching { FeedEventType.valueOf(typeStr) }.getOrDefault(FeedEventType.MEDAL_UNLOCKED)
            SocialFeedEvent(
                id = doc.id,
                userId = targetUserId,
                displayName = name,
                avatarColorHex = color,
                eventType = type,
                description = doc.getString("description") ?: "",
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                xpEarned = doc.getLong("xpEarned")?.toInt() ?: 0
            )
        } ?: emptyList()

        // Mutual rooms calculation
        val myRooms = roomRepository.observeJoinedRooms(currentUserId).firstOrNull() ?: emptyList()
        val targetRooms = roomRepository.observeJoinedRooms(targetUserId).firstOrNull() ?: emptyList()
        val myRoomIds = myRooms.map { it.id }.toSet()
        val mutualRooms = targetRooms.filter { myRoomIds.contains(it.id) }

        PublicProfile(
            userId = targetUserId,
            displayName = name,
            avatarColorHex = color,
            level = level,
            currentMedal = medal,
            currentStreak = streak,
            totalXp = xp,
            xpByCategory = xpByCategory,
            recentAchievements = recentAchievements,
            mutualRooms = mutualRooms,
            friendshipStatus = friendshipStatus
        )
    }
}
