package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.model.assignment.CompletionVerificationStatus
import com.questrings.domain.repository.AssignmentRepository
import java.util.UUID
import javax.inject.Inject

class SubmitCompletionUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(
        assignment: Assignment,
        userId: String,
        displayName: String,
        avatarColorHex: String,
        checkedItemIds: List<String> = emptyList()
    ): Result<AssignmentCompletion> = runCatching {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L

        // Validate not closed
        if (now > assignment.dueAt + oneDayMs) {
            error("Assignment is closed. No further completions are accepted.")
        }

        // Validate not already submitted
        val existing = assignmentRepository.getCurrentUserCompletion(assignment.id, userId).getOrNull()
        if (existing != null) {
            error("You have already submitted this assignment.")
        }

        // Validate checklist completion
        if (assignment.type == AssignmentType.CHECKLIST) {
            val requiredIds = assignment.checklistItems.map { it.id }.toSet()
            if (!checkedItemIds.containsAll(requiredIds)) {
                error("All checklist items must be checked before submitting.")
            }
        }

        val isLate = now > assignment.dueAt
        val status = if (assignment.requiresVerification) {
            CompletionVerificationStatus.PENDING
        } else {
            CompletionVerificationStatus.AUTO_APPROVED
        }

        val completion = AssignmentCompletion(
            id = UUID.randomUUID().toString(),
            assignmentId = assignment.id,
            roomId = assignment.roomId,
            userId = userId,
            displayName = displayName,
            avatarColorHex = avatarColorHex,
            completedAt = now,
            checkedItemIds = checkedItemIds,
            isLate = isLate,
            verificationStatus = status,
            rank = null,
            pointsAwarded = null,
            xpAwarded = null,
            speedBonusAwarded = null
        )

        assignmentRepository.submitCompletion(completion).getOrThrow()
        completion
    }
}
