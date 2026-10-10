package com.questrings.domain.repository

import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.assignment.RoomLeaderboardEntry
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun observeAssignmentsForRoom(roomId: String): Flow<List<Assignment>>
    fun observeAssignmentById(assignmentId: String): Flow<Assignment?>
    fun observeCompletionsForAssignment(assignmentId: String): Flow<List<AssignmentCompletion>>
    fun observePendingVerifications(roomId: String): Flow<List<AssignmentCompletion>>
    fun observeRoomLeaderboard(roomId: String): Flow<List<RoomLeaderboardEntry>>
    suspend fun createAssignment(assignment: Assignment): Result<Unit>
    suspend fun deleteAssignment(assignmentId: String): Result<Unit>
    suspend fun submitCompletion(completion: AssignmentCompletion): Result<Unit>
    suspend fun approveCompletion(completionId: String, assignmentId: String): Result<LeaderboardEntry>
    suspend fun rejectCompletion(completionId: String, reason: String): Result<Unit>
    suspend fun getCurrentUserCompletion(assignmentId: String, userId: String): Result<AssignmentCompletion?>
}
