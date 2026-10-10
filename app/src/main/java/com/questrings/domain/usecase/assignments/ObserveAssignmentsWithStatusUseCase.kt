package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class AssignmentWithStatus(
    val assignment: Assignment,
    val liveStatus: AssignmentStatus,
    val currentUserCompletion: AssignmentCompletion?,
    val completionCount: Int
)

class ObserveAssignmentsWithStatusUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    operator fun invoke(roomId: String, currentUserId: String): Flow<List<AssignmentWithStatus>> {
        return assignmentRepository.observeAssignmentsForRoom(roomId).combine(
            assignmentRepository.observePendingVerifications(roomId)
        ) { assignments, _ ->
            val now = System.currentTimeMillis()
            assignments.map { assignment ->
                val liveStatus = computeLiveStatus(assignment.dueAt, now)
                val userCompletion = assignmentRepository.getCurrentUserCompletion(assignment.id, currentUserId).getOrNull()
                AssignmentWithStatus(
                    assignment = assignment,
                    liveStatus = liveStatus,
                    currentUserCompletion = userCompletion,
                    completionCount = assignment.totalCompletions
                )
            }
        }
    }

    companion object {
        fun computeLiveStatus(dueAt: Long, now: Long = System.currentTimeMillis()): AssignmentStatus {
            val oneDayMs = 24 * 60 * 60 * 1000L
            return when {
                now > dueAt + oneDayMs -> AssignmentStatus.CLOSED
                now > dueAt -> AssignmentStatus.GRACE
                now >= dueAt - oneDayMs -> AssignmentStatus.ACTIVE
                else -> AssignmentStatus.UPCOMING
            }
        }
    }
}
