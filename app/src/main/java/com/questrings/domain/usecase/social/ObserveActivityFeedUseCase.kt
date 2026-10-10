package com.questrings.domain.usecase.social

import com.questrings.domain.model.social.SocialFeedEvent
import com.questrings.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ObserveActivityFeedUseCase @Inject constructor(
    private val socialRepository: SocialRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId: String): Flow<List<SocialFeedEvent>> {
        return socialRepository.observeFriends(userId).flatMapLatest { friends ->
            val friendIds = friends.map { it.userId }
            socialRepository.observeActivityFeed(userId, friendIds)
        }
    }
}
