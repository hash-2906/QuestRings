package com.questrings.domain.usecase.social

import com.questrings.domain.repository.SocialRepository
import javax.inject.Inject

class ToggleReactionUseCase @Inject constructor(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(
        eventId: String,
        emoji: String,
        userId: String,
        hasReacted: Boolean
    ): Result<Unit> {
        return if (hasReacted) {
            socialRepository.removeReaction(eventId, emoji, userId)
        } else {
            socialRepository.addReaction(eventId, emoji, userId)
        }
    }
}
