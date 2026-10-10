package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.repository.AssignmentRepository
import javax.inject.Inject

class ApproveCompletionUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(
        completionId: String,
        assignmentId: String
    ): Result<LeaderboardEntry> {
        return assignmentRepository.approveCompletion(completionId, assignmentId)
    }
}
