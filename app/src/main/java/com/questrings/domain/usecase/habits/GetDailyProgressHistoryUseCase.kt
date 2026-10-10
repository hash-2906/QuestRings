package com.questrings.domain.usecase.habits

import com.questrings.domain.model.DailyProgress
import com.questrings.domain.repository.StatsRepository
import javax.inject.Inject

class GetDailyProgressHistoryUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(
        userId: String,
        from: String,
        to: String
    ): Result<List<DailyProgress>> {
        return statsRepository.getDailyProgressHistory(userId, from, to)
    }
}
