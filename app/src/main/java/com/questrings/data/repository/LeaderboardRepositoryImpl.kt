package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow
import com.questrings.domain.repository.LeaderboardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LeaderboardRepository {

    override fun observeLeaderboard(
        scope: LeaderboardScope,
        timeWindow: LeaderboardTimeWindow,
        currentUserId: String,
        friendIds: List<String>
    ): Flow<List<LeaderboardEntry>> = callbackFlow {
        val xpField = when (timeWindow) {
            LeaderboardTimeWindow.WEEKLY -> "stats.xpWeekly"
            LeaderboardTimeWindow.MONTHLY -> "stats.xpMonthly"
            LeaderboardTimeWindow.ALL_TIME -> "xp"
        }

        if (scope == LeaderboardScope.FRIENDS) {
            val targetUids = (friendIds + currentUserId).distinct().take(30)
            if (targetUids.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            val registration = firestore.collection("users")
                .whereIn("uid", targetUids)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val entries = snapshot.documents.map { doc ->
                        val uid = doc.getString("uid") ?: doc.id
                        val name = doc.getString("displayName") ?: "Adventurer"
                        val color = doc.getString("avatarColorHex") ?: "#7E5DFF"
                        val xp = when (timeWindow) {
                            LeaderboardTimeWindow.WEEKLY -> doc.getLong("stats.xpWeekly")?.toInt()
                                ?: doc.getLong("xpWeekly")?.toInt()
                                ?: ((doc.getLong("xp")?.toInt() ?: 0) / 4)
                            LeaderboardTimeWindow.MONTHLY -> doc.getLong("stats.xpMonthly")?.toInt()
                                ?: doc.getLong("xpMonthly")?.toInt()
                                ?: ((doc.getLong("xp")?.toInt() ?: 0) / 2)
                            LeaderboardTimeWindow.ALL_TIME -> doc.getLong("xp")?.toInt() ?: 0
                        }

                        Triple(uid, name, color) to xp
                    }
                        .sortedByDescending { it.second }
                        .mapIndexed { index, (userTriple, xpVal) ->
                            LeaderboardEntry(
                                rank = index + 1,
                                userId = userTriple.first,
                                displayName = userTriple.second,
                                avatarColorHex = userTriple.third,
                                completedAt = System.currentTimeMillis(),
                                pointsAwarded = 0,
                                xpAwarded = xpVal,
                                isLate = false,
                                speedBonusAwarded = 0,
                                isCurrentUser = userTriple.first == currentUserId
                            )
                        }

                    trySend(entries)
                }
            awaitClose { registration.remove() }
        } else {
            // GLOBAL SCOPE
            val registration = firestore.collection("users")
                .orderBy(xpField, Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        // Fallback if index on nested stats isn't built yet
                        firestore.collection("users")
                            .orderBy("xp", Query.Direction.DESCENDING)
                            .limit(100)
                            .get()
                            .addOnSuccessListener { fallbackSnap ->
                                val fallbackEntries = fallbackSnap.documents.mapIndexed { index, doc ->
                                    val uid = doc.getString("uid") ?: doc.id
                                    val name = doc.getString("displayName") ?: "Adventurer"
                                    val color = doc.getString("avatarColorHex") ?: "#7E5DFF"
                                    val rawXp = doc.getLong("xp")?.toInt() ?: 0
                                    val xp = when (timeWindow) {
                                        LeaderboardTimeWindow.WEEKLY -> rawXp / 4
                                        LeaderboardTimeWindow.MONTHLY -> rawXp / 2
                                        LeaderboardTimeWindow.ALL_TIME -> rawXp
                                    }

                                    LeaderboardEntry(
                                        rank = index + 1,
                                        userId = uid,
                                        displayName = name,
                                        avatarColorHex = color,
                                        completedAt = System.currentTimeMillis(),
                                        pointsAwarded = 0,
                                        xpAwarded = xp,
                                        isLate = false,
                                        speedBonusAwarded = 0,
                                        isCurrentUser = uid == currentUserId
                                    )
                                }
                                trySend(fallbackEntries)
                            }
                        return@addSnapshotListener
                    }

                    val entries = snapshot.documents.mapIndexed { index, doc ->
                        val uid = doc.getString("uid") ?: doc.id
                        val name = doc.getString("displayName") ?: "Adventurer"
                        val color = doc.getString("avatarColorHex") ?: "#7E5DFF"
                        val xp = when (timeWindow) {
                            LeaderboardTimeWindow.WEEKLY -> doc.getLong("stats.xpWeekly")?.toInt()
                                ?: doc.getLong("xpWeekly")?.toInt()
                                ?: ((doc.getLong("xp")?.toInt() ?: 0) / 4)
                            LeaderboardTimeWindow.MONTHLY -> doc.getLong("stats.xpMonthly")?.toInt()
                                ?: doc.getLong("xpMonthly")?.toInt()
                                ?: ((doc.getLong("xp")?.toInt() ?: 0) / 2)
                            LeaderboardTimeWindow.ALL_TIME -> doc.getLong("xp")?.toInt() ?: 0
                        }

                        LeaderboardEntry(
                            rank = index + 1,
                            userId = uid,
                            displayName = name,
                            avatarColorHex = color,
                            completedAt = System.currentTimeMillis(),
                            pointsAwarded = 0,
                            xpAwarded = xp,
                            isLate = false,
                            speedBonusAwarded = 0,
                            isCurrentUser = uid == currentUserId
                        )
                    }
                    trySend(entries)
                }
            awaitClose { registration.remove() }
        }
    }
}
