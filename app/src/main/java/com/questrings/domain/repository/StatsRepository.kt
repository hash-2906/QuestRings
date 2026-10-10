package com.questrings.domain.repository

import com.questrings.domain.model.DailyProgress
import com.questrings.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun observeUserStats(userId: String): Flow<UserStats>
    suspend fun updateStats(stats: UserStats): Result<Unit>
    suspend fun getDailyProgressHistory(
        userId: String, from: String, to: String
    ): Result<List<DailyProgress>>
}
