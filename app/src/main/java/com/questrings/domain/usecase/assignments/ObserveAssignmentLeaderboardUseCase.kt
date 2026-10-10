package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.CompletionVerificationStatus
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveAssignmentLeaderboardUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    operator fun invoke(assignmentId: String, currentUserId: String): Flow<List<LeaderboardEntry>> {
        return assignmentRepository.observeCompletionsForAssignment(assignmentId).map { completions ->
            completions
                .filter {
                    it.verificationStatus == CompletionVerificationStatus.APPROVED ||
                    it.verificationStatus == CompletionVerificationStatus.AUTO_APPROVED
                }
                .sortedBy { it.rank ?: Int.MAX_VALUE }
                .mapIndexed { index, comp ->
                    LeaderboardEntry(
                        rank = comp.rank ?: (index + 1),
                        userId = comp.userId,
                        displayName = comp.displayName,
                        avatarColorHex = comp.avatarColorHex,
                        completedAt = comp.completedAt,
                        pointsAwarded = comp.pointsAwarded ?: 0,
                        xpAwarded = comp.xpAwarded ?: 0,
                        isLate = comp.isLate,
                        speedBonusAwarded = comp.speedBonusAwarded ?: 0,
                        isCurrentUser = comp.userId == currentUserId
                    )
                }
        }
    }
}
