package com.questrings.domain.usecase.assignments

import com.questrings.domain.repository.AssignmentRepository
import javax.inject.Inject

class RejectCompletionUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(
        completionId: String,
        reason: String
    ): Result<Unit> {
        return assignmentRepository.rejectCompletion(completionId, reason)
    }
}
