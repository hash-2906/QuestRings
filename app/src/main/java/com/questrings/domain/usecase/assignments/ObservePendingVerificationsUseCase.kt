package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePendingVerificationsUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    operator fun invoke(roomId: String): Flow<List<AssignmentCompletion>> {
        return assignmentRepository.observePendingVerifications(roomId)
    }
}
