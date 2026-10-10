package com.questrings.presentation.social

import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.social.FriendRequest
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow
import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.model.social.SocialFeedEvent

enum class SocialTab { FEED, LEADERBOARD }

data class SocialUiState(
    val currentTab: SocialTab = SocialTab.FEED,
    val feedEvents: List<SocialFeedEvent> = emptyList(),
    val leaderboardScope: LeaderboardScope = LeaderboardScope.FRIENDS,
    val leaderboardWindow: LeaderboardTimeWindow = LeaderboardTimeWindow.WEEKLY,
    val leaderboardEntries: List<LeaderboardEntry> = emptyList(),
    val pendingRequestsCount: Int = 0,
    val friends: List<PublicProfile> = emptyList(),
    val pendingRequests: List<FriendRequest> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<PublicProfile> = emptyList(),
    val isSearching: Boolean = false,
    val showFriendsSheet: Boolean = false,
    val currentUserId: String = "",
    val isLoading: Boolean = true
)
