package com.questrings.domain.usecase.social

import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow
import com.questrings.domain.repository.LeaderboardRepository
import com.questrings.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveLeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val socialRepository: SocialRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        scope: LeaderboardScope,
        timeWindow: LeaderboardTimeWindow,
        currentUserId: String
    ): Flow<List<LeaderboardEntry>> {
        return if (scope == LeaderboardScope.FRIENDS) {
            socialRepository.observeFriends(currentUserId).flatMapLatest { friends ->
                val friendIds = friends.map { it.userId }
                leaderboardRepository.observeLeaderboard(scope, timeWindow, currentUserId, friendIds)
            }
        } else {
            leaderboardRepository.observeLeaderboard(scope, timeWindow, currentUserId, emptyList())
        }
    }
}
