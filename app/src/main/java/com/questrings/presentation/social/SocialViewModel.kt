package com.questrings.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow
import com.questrings.domain.model.social.PublicProfile
import com.questrings.domain.repository.AuthRepository
import com.questrings.domain.usecase.social.ObserveActivityFeedUseCase
import com.questrings.domain.usecase.social.ObserveFriendGraphUseCase
import com.questrings.domain.usecase.social.ObserveLeaderboardUseCase
import com.questrings.domain.usecase.social.SearchUsersUseCase
import com.questrings.domain.usecase.social.ToggleReactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SocialViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val observeActivityFeedUseCase: ObserveActivityFeedUseCase,
    private val toggleReactionUseCase: ToggleReactionUseCase,
    private val observeLeaderboardUseCase: ObserveLeaderboardUseCase,
    private val observeFriendGraphUseCase: ObserveFriendGraphUseCase,
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val _currentTab = MutableStateFlow(SocialTab.FEED)
    private val _leaderboardScope = MutableStateFlow(LeaderboardScope.FRIENDS)
    private val _leaderboardWindow = MutableStateFlow(LeaderboardTimeWindow.WEEKLY)
    private val _showFriendsSheet = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<PublicProfile>>(emptyList())
    private val _isSearching = MutableStateFlow(false)

    private var searchJob: Job? = null

    val uiState: StateFlow<SocialUiState> = authRepository.currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(SocialUiState(isLoading = false))
        } else {
            val feedFlow = observeActivityFeedUseCase(user.uid)
            val friendsFlow = observeFriendGraphUseCase.observeFriends(user.uid)
            val requestsFlow = observeFriendGraphUseCase.observeRequests(user.uid)

            val leaderboardFlow = combine(
                _leaderboardScope,
                _leaderboardWindow
            ) { scope, window -> scope to window }.flatMapLatest { (scope, window) ->
                observeLeaderboardUseCase(scope, window, user.uid)
            }

            combine(
                _currentTab,
                _leaderboardScope,
                _leaderboardWindow,
                feedFlow,
                leaderboardFlow,
                friendsFlow,
                requestsFlow
            ) { tab, scope, window, feed, leaderboard, friends, requests ->
                SocialUiState(
                    currentTab = tab,
                    feedEvents = feed,
                    leaderboardScope = scope,
                    leaderboardWindow = window,
                    leaderboardEntries = leaderboard,
                    pendingRequestsCount = requests.size,
                    friends = friends,
                    pendingRequests = requests,
                    searchQuery = _searchQuery.value,
                    searchResults = _searchResults.value,
                    isSearching = _isSearching.value,
                    showFriendsSheet = _showFriendsSheet.value,
                    currentUserId = user.uid,
                    isLoading = false
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SocialUiState()
    )

    fun setTab(tab: SocialTab) {
        _currentTab.value = tab
    }

    fun setLeaderboardScope(scope: LeaderboardScope) {
        _leaderboardScope.value = scope
    }

    fun setLeaderboardWindow(window: LeaderboardTimeWindow) {
        _leaderboardWindow.value = window
    }

    fun toggleReaction(eventId: String, emoji: String) {
        val currentUserId = uiState.value.currentUserId
        if (currentUserId.isBlank()) return

        val event = uiState.value.feedEvents.firstOrNull { it.id == eventId } ?: return
        val hasReacted = event.reactions[emoji]?.contains(currentUserId) == true

        viewModelScope.launch {
            toggleReactionUseCase(
                eventId = eventId,
                emoji = emoji,
                userId = currentUserId,
                hasReacted = hasReacted
            )
        }
    }

    fun openFriendsSheet() {
        _showFriendsSheet.value = true
    }

    fun closeFriendsSheet() {
        _showFriendsSheet.value = false
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(350) // Debounce
            val results = searchUsersUseCase(query.trim(), uiState.value.currentUserId).getOrDefault(emptyList())
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            observeFriendGraphUseCase.sendRequest(
                fromUserId = uiState.value.currentUserId,
                toUserId = targetUserId
            )
        }
    }

    fun acceptFriendRequest(requestId: String, fromUserId: String) {
        viewModelScope.launch {
            observeFriendGraphUseCase.acceptRequest(
                requestId = requestId,
                fromUserId = fromUserId,
                toUserId = uiState.value.currentUserId
            )
        }
    }

    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            observeFriendGraphUseCase.rejectRequest(
                requestId = requestId,
                userId = uiState.value.currentUserId
            )
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            observeFriendGraphUseCase.removeFriend(
                userId = uiState.value.currentUserId,
                friendId = friendId
            )
        }
    }
}
