package com.questrings.domain.usecase.xp

import com.questrings.domain.model.xp.XpSummary
import com.questrings.domain.repository.XpRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveXpSummaryUseCase @Inject constructor(
    private val xpRepository: XpRepository
) {
    operator fun invoke(userId: String): Flow<XpSummary> =
        xpRepository.observeXpSummary(userId)
}
