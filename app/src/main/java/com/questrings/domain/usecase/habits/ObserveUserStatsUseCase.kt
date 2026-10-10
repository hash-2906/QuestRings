package com.questrings.domain.usecase.habits

import com.questrings.domain.model.UserStats
import com.questrings.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(userId: String): Flow<UserStats> {
        return statsRepository.observeUserStats(userId)
    }
}
