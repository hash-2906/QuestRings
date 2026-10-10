package com.questrings.domain.usecase.xp

import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.repository.XpRepository
import javax.inject.Inject

class GetXpEventDetailUseCase @Inject constructor(
    private val xpRepository: XpRepository
) {
    suspend operator fun invoke(eventId: String): Result<XpEvent?> =
        xpRepository.getXpEventById(eventId)
}
