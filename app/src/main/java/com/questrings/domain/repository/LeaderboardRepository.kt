package com.questrings.domain.repository

import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun observeLeaderboard(
        scope: LeaderboardScope,
        timeWindow: LeaderboardTimeWindow,
        currentUserId: String,
        friendIds: List<String> = emptyList()
    ): Flow<List<LeaderboardEntry>>
}
