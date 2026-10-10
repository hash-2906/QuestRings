package com.questrings.domain.usecase.xp

import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.repository.XpRepository
import com.questrings.domain.repository.XpSourceFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveXpHistoryUseCase @Inject constructor(
    private val xpRepository: XpRepository
) {
    operator fun invoke(
        userId: String,
        filter: XpSourceFilter = XpSourceFilter.ALL,
        limit: Int = 50
    ): Flow<List<XpEvent>> = xpRepository.observeXpHistory(userId, filter, limit)
}
