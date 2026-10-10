package com.questrings.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.questrings.data.notifications.AssignmentNotificationScheduler
import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.model.assignment.ChecklistItem
import com.questrings.domain.model.assignment.CompletionVerificationStatus
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.assignment.RoomLeaderboardEntry
import com.questrings.domain.repository.AssignmentRepository
import com.questrings.domain.scoring.AssignmentScoringEngine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val scoringEngine: AssignmentScoringEngine,
    private val notificationScheduler: AssignmentNotificationScheduler
) : AssignmentRepository {

    override fun observeAssignmentsForRoom(roomId: String): Flow<List<Assignment>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .document(roomId)
            .collection("assignments")
            .orderBy("dueAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    mapDocToAssignment(doc.id, roomId, doc.data ?: emptyMap())
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun observeAssignmentById(assignmentId: String): Flow<Assignment?> = callbackFlow {
        val listener = firestore.collectionGroup("assignments")
            .whereEqualTo("id", assignmentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val doc = snapshot.documents.firstOrNull()
                val roomId = doc?.reference?.parent?.parent?.id ?: ""
                val assignment = doc?.let { mapDocToAssignment(it.id, roomId, it.data ?: emptyMap()) }
                trySend(assignment)
            }
        awaitClose { listener.remove() }
    }

    override fun observeCompletionsForAssignment(assignmentId: String): Flow<List<AssignmentCompletion>> = callbackFlow {
        val listener = firestore.collectionGroup("completions")
            .whereEqualTo("assignmentId", assignmentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    mapDocToCompletion(doc.id, doc.data ?: emptyMap())
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun observePendingVerifications(roomId: String): Flow<List<AssignmentCompletion>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .document(roomId)
            .collection("completions")
            .whereEqualTo("verificationStatus", CompletionVerificationStatus.PENDING.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    mapDocToCompletion(doc.id, doc.data ?: emptyMap())
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun observeRoomLeaderboard(roomId: String): Flow<List<RoomLeaderboardEntry>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .document(roomId)
            .collection("members")
            .orderBy("totalPoints", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapIndexedNotNull { index, doc ->
                    val data = doc.data ?: return@mapIndexedNotNull null
                    RoomLeaderboardEntry(
                        rank = index + 1,
                        userId = doc.id,
                        displayName = data["displayName"] as? String ?: "Adventurer",
                        avatarColorHex = data["avatarColorHex"] as? String ?: "#7E5DFF",
                        totalPoints = (data["totalPoints"] as? Long)?.toInt() ?: 0,
                        totalXpEarned = (data["totalXpEarned"] as? Long)?.toInt() ?: 0,
                        assignmentsCompleted = (data["assignmentsCompleted"] as? Long)?.toInt() ?: 0,
                        isCurrentUser = false
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createAssignment(assignment: Assignment): Result<Unit> = runCatching {
        val checklistData = assignment.checklistItems.map {
            mapOf("id" to it.id, "label" to it.label, "sortOrder" to it.sortOrder)
        }
        val map = mapOf(
            "id" to assignment.id,
            "roomId" to assignment.roomId,
            "createdBy" to assignment.createdBy,
            "title" to assignment.title,
            "description" to assignment.description,
            "type" to assignment.type.name,
            "checklistItems" to checklistData,
            "status" to assignment.status.name,
            "requiresVerification" to assignment.requiresVerification,
            "createdAt" to assignment.createdAt,
            "dueAt" to assignment.dueAt,
            "totalCompletions" to assignment.totalCompletions,
            "maxPoints" to assignment.maxPoints
        )
        firestore.collection("rooms")
            .document(assignment.roomId)
            .collection("assignments")
            .document(assignment.id)
            .set(map)
            .await()

        notificationScheduler.scheduleAssignmentGraceWorker(assignment)
    }

    override suspend fun deleteAssignment(assignmentId: String): Result<Unit> = runCatching {
        // Query to find parent room
        val query = firestore.collectionGroup("assignments")
            .whereEqualTo("id", assignmentId)
            .get()
            .await()

        val doc = query.documents.firstOrNull() ?: return@runCatching
        val roomId = doc.reference.parent.parent?.id ?: return@runCatching

        val batch = firestore.batch()
        batch.delete(doc.reference)

        // Delete completions
        val completions = firestore.collection("rooms")
            .document(roomId)
            .collection("completions")
            .whereEqualTo("assignmentId", assignmentId)
            .get()
            .await()

        for (comp in completions.documents) {
            batch.delete(comp.reference)
        }

        batch.commit().await()
        notificationScheduler.cancelAssignmentWork(assignmentId)
    }

    override suspend fun submitCompletion(completion: AssignmentCompletion): Result<Unit> = runCatching {
        val completionRef = firestore.collection("rooms")
            .document(completion.roomId)
            .collection("completions")
            .document(completion.id)

        val assignmentRef = firestore.collection("rooms")
            .document(completion.roomId)
            .collection("assignments")
            .document(completion.assignmentId)

        val memberRef = firestore.collection("rooms")
            .document(completion.roomId)
            .collection("members")
            .document(completion.userId)

        if (completion.verificationStatus == CompletionVerificationStatus.AUTO_APPROVED) {
            firestore.runTransaction { tx ->
                val assignmentDoc = tx.get(assignmentRef)
                val currentCount = (assignmentDoc.getLong("totalCompletions") ?: 0L).toInt()
                val createdAt = assignmentDoc.getLong("createdAt") ?: System.currentTimeMillis()
                val dueAt = assignmentDoc.getLong("dueAt") ?: System.currentTimeMillis()
                val title = assignmentDoc.getString("title") ?: "Quest"

                val rank = currentCount + 1
                val (points, speed) = scoringEngine.computeTotalPoints(
                    rank = rank,
                    completedAt = completion.completedAt,
                    createdAt = createdAt,
                    dueAt = dueAt,
                    isLate = completion.isLate
                )
                val xp = scoringEngine.computeRoomAssignmentXp(points)

                tx.update(assignmentRef, "totalCompletions", FieldValue.increment(1))
                tx.set(
                    completionRef,
                    mapOf(
                        "id" to completion.id,
                        "assignmentId" to completion.assignmentId,
                        "roomId" to completion.roomId,
                        "userId" to completion.userId,
                        "displayName" to completion.displayName,
                        "avatarColorHex" to completion.avatarColorHex,
                        "completedAt" to completion.completedAt,
                        "checkedItemIds" to completion.checkedItemIds,
                        "isLate" to completion.isLate,
                        "verificationStatus" to CompletionVerificationStatus.APPROVED.name,
                        "rank" to rank,
                        "pointsAwarded" to points,
                        "xpAwarded" to xp,
                        "speedBonusAwarded" to speed
                    )
                )

                tx.update(
                    memberRef,
                    mapOf(
                        "totalPoints" to FieldValue.increment(points.toLong()),
                        "totalXpEarned" to FieldValue.increment(xp.toLong()),
                        "assignmentsCompleted" to FieldValue.increment(1L)
                    )
                )
            }.await()

            notificationScheduler.sendVerificationApprovedNotification(
                assignmentTitle = "Party Quest",
                rank = 1,
                points = 100,
                xp = 150
            )
        } else {
            // Save as PENDING
            completionRef.set(
                mapOf(
                    "id" to completion.id,
                    "assignmentId" to completion.assignmentId,
                    "roomId" to completion.roomId,
                    "userId" to completion.userId,
                    "displayName" to completion.displayName,
                    "avatarColorHex" to completion.avatarColorHex,
                    "completedAt" to completion.completedAt,
                    "checkedItemIds" to completion.checkedItemIds,
                    "isLate" to completion.isLate,
                    "verificationStatus" to CompletionVerificationStatus.PENDING.name,
                    "rank" to null,
                    "pointsAwarded" to null,
                    "xpAwarded" to null,
                    "speedBonusAwarded" to null
                )
            ).await()
        }
    }

    override suspend fun approveCompletion(completionId: String, assignmentId: String): Result<LeaderboardEntry> = runCatching {
        // Find completion
        val completionQuery = firestore.collectionGroup("completions")
            .whereEqualTo("id", completionId)
            .get()
            .await()

        val completionDoc = completionQuery.documents.firstOrNull() ?: error("Completion not found")
        val roomId = completionDoc.getString("roomId") ?: error("Room not found")
        val userId = completionDoc.getString("userId") ?: error("User not found")
        val displayName = completionDoc.getString("displayName") ?: "Adventurer"
        val avatarColorHex = completionDoc.getString("avatarColorHex") ?: "#7E5DFF"
        val isLate = completionDoc.getBoolean("isLate") ?: false
        val completedAt = completionDoc.getLong("completedAt") ?: System.currentTimeMillis()

        val assignmentRef = firestore.collection("rooms")
            .document(roomId)
            .collection("assignments")
            .document(assignmentId)

        val memberRef = firestore.collection("rooms")
            .document(roomId)
            .collection("members")
            .document(userId)

        var finalRank = 1
        var finalPoints = 100
        var finalXp = 150
        var finalSpeed = 0

        firestore.runTransaction { tx ->
            val assignmentDoc = tx.get(assignmentRef)
            val currentCount = (assignmentDoc.getLong("totalCompletions") ?: 0L).toInt()
            val createdAt = assignmentDoc.getLong("createdAt") ?: System.currentTimeMillis()
            val dueAt = assignmentDoc.getLong("dueAt") ?: System.currentTimeMillis()

            finalRank = currentCount + 1
            val (points, speed) = scoringEngine.computeTotalPoints(
                rank = finalRank,
                completedAt = completedAt,
                createdAt = createdAt,
                dueAt = dueAt,
                isLate = isLate
            )
            finalPoints = points
            finalSpeed = speed
            finalXp = scoringEngine.computeRoomAssignmentXp(finalPoints)

            tx.update(assignmentRef, "totalCompletions", FieldValue.increment(1))
            tx.update(
                completionDoc.reference,
                mapOf(
                    "verificationStatus" to CompletionVerificationStatus.APPROVED.name,
                    "rank" to finalRank,
                    "pointsAwarded" to finalPoints,
                    "xpAwarded" to finalXp,
                    "speedBonusAwarded" to finalSpeed
                )
            )

            tx.update(
                memberRef,
                mapOf(
                    "totalPoints" to FieldValue.increment(finalPoints.toLong()),
                    "totalXpEarned" to FieldValue.increment(finalXp.toLong()),
                    "assignmentsCompleted" to FieldValue.increment(1L)
                )
            )
        }.await()

        notificationScheduler.sendVerificationApprovedNotification(
            assignmentTitle = "Quest Verified",
            rank = finalRank,
            points = finalPoints,
            xp = finalXp
        )

        LeaderboardEntry(
            rank = finalRank,
            userId = userId,
            displayName = displayName,
            avatarColorHex = avatarColorHex,
            completedAt = completedAt,
            pointsAwarded = finalPoints,
            xpAwarded = finalXp,
            isLate = isLate,
            speedBonusAwarded = finalSpeed,
            isCurrentUser = false
        )
    }

    override suspend fun rejectCompletion(completionId: String, reason: String): Result<Unit> = runCatching {
        val completionQuery = firestore.collectionGroup("completions")
            .whereEqualTo("id", completionId)
            .get()
            .await()

        val completionDoc = completionQuery.documents.firstOrNull() ?: error("Completion not found")
        completionDoc.reference.update(
            mapOf(
                "verificationStatus" to CompletionVerificationStatus.REJECTED.name,
                "rejectionReason" to reason
            )
        ).await()

        notificationScheduler.sendVerificationRejectedNotification("Quest Submission", reason)
    }

    override suspend fun getCurrentUserCompletion(assignmentId: String, userId: String): Result<AssignmentCompletion?> = runCatching {
        val query = firestore.collectionGroup("completions")
            .whereEqualTo("assignmentId", assignmentId)
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .await()

        val doc = query.documents.firstOrNull() ?: return@runCatching null
        mapDocToCompletion(doc.id, doc.data ?: emptyMap())
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapDocToAssignment(id: String, roomId: String, data: Map<String, Any>): Assignment {
        val rawItems = data["checklistItems"] as? List<Map<String, Any>> ?: emptyList()
        val checklist = rawItems.map { item ->
            ChecklistItem(
                id = item["id"] as? String ?: "",
                label = item["label"] as? String ?: "",
                sortOrder = (item["sortOrder"] as? Long)?.toInt() ?: 0
            )
        }
        return Assignment(
            id = id,
            roomId = roomId,
            createdBy = data["createdBy"] as? String ?: "",
            title = data["title"] as? String ?: "",
            description = data["description"] as? String ?: "",
            type = runCatching { AssignmentType.valueOf(data["type"] as String) }.getOrDefault(AssignmentType.SIMPLE),
            checklistItems = checklist,
            status = runCatching { AssignmentStatus.valueOf(data["status"] as String) }.getOrDefault(AssignmentStatus.UPCOMING),
            requiresVerification = data["requiresVerification"] as? Boolean ?: false,
            createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis(),
            dueAt = (data["dueAt"] as? Long) ?: (System.currentTimeMillis() + 86400000L),
            totalCompletions = (data["totalCompletions"] as? Long)?.toInt() ?: 0,
            maxPoints = (data["maxPoints"] as? Long)?.toInt() ?: 100
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapDocToCompletion(id: String, data: Map<String, Any>): AssignmentCompletion {
        val checked = data["checkedItemIds"] as? List<String> ?: emptyList()
        val statusStr = data["verificationStatus"] as? String ?: CompletionVerificationStatus.AUTO_APPROVED.name
        val status = runCatching { CompletionVerificationStatus.valueOf(statusStr) }.getOrDefault(CompletionVerificationStatus.AUTO_APPROVED)

        return AssignmentCompletion(
            id = id,
            assignmentId = data["assignmentId"] as? String ?: "",
            roomId = data["roomId"] as? String ?: "",
            userId = data["userId"] as? String ?: "",
            displayName = data["displayName"] as? String ?: "Adventurer",
            avatarColorHex = data["avatarColorHex"] as? String ?: "#7E5DFF",
            completedAt = (data["completedAt"] as? Long) ?: System.currentTimeMillis(),
            checkedItemIds = checked,
            isLate = data["isLate"] as? Boolean ?: false,
            verificationStatus = status,
            rank = (data["rank"] as? Long)?.toInt(),
            pointsAwarded = (data["pointsAwarded"] as? Long)?.toInt(),
            xpAwarded = (data["xpAwarded"] as? Long)?.toInt(),
            speedBonusAwarded = (data["speedBonusAwarded"] as? Long)?.toInt(),
            rejectionReason = data["rejectionReason"] as? String
        )
    }
}
