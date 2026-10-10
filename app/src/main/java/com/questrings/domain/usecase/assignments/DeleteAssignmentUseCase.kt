package com.questrings.domain.usecase.assignments

import com.questrings.domain.repository.AssignmentRepository
import javax.inject.Inject

class DeleteAssignmentUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(assignmentId: String): Result<Unit> {
        return assignmentRepository.deleteAssignment(assignmentId)
    }
}
