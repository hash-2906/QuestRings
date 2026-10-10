package com.questrings.domain.repository

import com.questrings.domain.model.social.FriendRequest
import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.model.social.SocialFeedEvent
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun observeFriends(userId: String): Flow<List<PublicProfile>>
    fun observeFriendRequests(userId: String): Flow<List<FriendRequest>>
    suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Result<Unit>
    suspend fun acceptFriendRequest(requestId: String, fromUserId: String, toUserId: String): Result<Unit>
    suspend fun rejectFriendRequest(requestId: String, userId: String): Result<Unit>
    suspend fun removeFriend(userId: String, friendId: String): Result<Unit>
    suspend fun searchUsers(query: String, currentUserId: String): Result<List<PublicProfile>>
    
    // Activity Feed
    fun observeActivityFeed(userId: String, friendIds: List<String>): Flow<List<SocialFeedEvent>>
    suspend fun addReaction(eventId: String, emoji: String, userId: String): Result<Unit>
    suspend fun removeReaction(eventId: String, emoji: String, userId: String): Result<Unit>
    
    // Publishing to feed
    suspend fun publishFeedEvent(event: SocialFeedEvent): Result<Unit>
}
