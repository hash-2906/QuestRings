package com.questrings.domain.usecase.social

import com.questrings.domain.model.social.FriendRequest
import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.repository.SocialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFriendGraphUseCase @Inject constructor(
    private val socialRepository: SocialRepository
) {
    fun observeFriends(userId: String): Flow<List<PublicProfile>> =
        socialRepository.observeFriends(userId)

    fun observeRequests(userId: String): Flow<List<FriendRequest>> =
        socialRepository.observeFriendRequests(userId)

    suspend fun sendRequest(fromUserId: String, toUserId: String): Result<Unit> =
        socialRepository.sendFriendRequest(fromUserId, toUserId)

    suspend fun acceptRequest(requestId: String, fromUserId: String, toUserId: String): Result<Unit> =
        socialRepository.acceptFriendRequest(requestId, fromUserId, toUserId)

    suspend fun rejectRequest(requestId: String, userId: String): Result<Unit> =
        socialRepository.rejectFriendRequest(requestId, userId)

    suspend fun removeFriend(userId: String, friendId: String): Result<Unit> =
        socialRepository.removeFriend(userId, friendId)
}
