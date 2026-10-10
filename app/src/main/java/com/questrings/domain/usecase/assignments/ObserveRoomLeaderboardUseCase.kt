package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.RoomLeaderboardEntry
import com.questrings.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveRoomLeaderboardUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    operator fun invoke(roomId: String, currentUserId: String): Flow<List<RoomLeaderboardEntry>> {
        return assignmentRepository.observeRoomLeaderboard(roomId).map { entries ->
            entries
                .sortedByDescending { it.totalPoints }
                .mapIndexed { index, entry ->
                    entry.copy(
                        rank = index + 1,
                        isCurrentUser = entry.userId == currentUserId
                    )
                }
        }
    }
}
