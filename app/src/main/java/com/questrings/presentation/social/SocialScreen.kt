package com.questrings.presentation.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.presentation.social.feed.FeedView
import com.questrings.presentation.social.friends.FriendsManagerSheet
import com.questrings.presentation.social.leaderboard.GlobalLeaderboardView
import kotlinx.coroutines.launch

@Composable
fun SocialScreen(
    onNavigateToPublicProfile: (userId: String) -> Unit,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SocialContent(
        uiState = uiState,
        onTabSelected = viewModel::setTab,
        onScopeSelected = viewModel::setLeaderboardScope,
        onTimeWindowSelected = viewModel::setLeaderboardWindow,
        onToggleReaction = viewModel::toggleReaction,
        onOpenFriendsSheet = viewModel::openFriendsSheet,
        onCloseFriendsSheet = viewModel::closeFriendsSheet,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSendFriendRequest = viewModel::sendFriendRequest,
        onAcceptRequest = viewModel::acceptFriendRequest,
        onRejectRequest = viewModel::rejectFriendRequest,
        onRemoveFriend = viewModel::removeFriend,
        onNavigateToPublicProfile = onNavigateToPublicProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialContent(
    uiState: SocialUiState,
    onTabSelected: (SocialTab) -> Unit,
    onScopeSelected: (com.questrings.domain.model.social.LeaderboardScope) -> Unit,
    onTimeWindowSelected: (com.questrings.domain.model.social.LeaderboardTimeWindow) -> Unit,
    onToggleReaction: (eventId: String, emoji: String) -> Unit,
    onOpenFriendsSheet: () -> Unit,
    onCloseFriendsSheet: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSendFriendRequest: (userId: String) -> Unit,
    onAcceptRequest: (requestId: String, fromUserId: String) -> Unit,
    onRejectRequest: (requestId: String) -> Unit,
    onRemoveFriend: (friendId: String) -> Unit,
    onNavigateToPublicProfile: (userId: String) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = uiState.currentTab.ordinal) { 2 }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val selectedTab = if (pagerState.currentPage == 0) SocialTab.FEED else SocialTab.LEADERBOARD
        if (selectedTab != uiState.currentTab) {
            onTabSelected(selectedTab)
        }
    }

    LaunchedEffect(uiState.currentTab) {
        if (pagerState.currentPage != uiState.currentTab.ordinal) {
            pagerState.animateScrollToPage(uiState.currentTab.ordinal)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "COMMUNITY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onOpenFriendsSheet) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Friends",
                                tint = TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            if (uiState.pendingRequestsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(VitalityRed)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundPrimary)
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RoyalPurple)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Row (ACTIVITY | LEADERBOARD)
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = BackgroundPrimary,
                    contentColor = TextPrimary,
                    divider = {
                        Box(
                            modifier = Modifier
                                .size(width = 0.dp, height = 1.dp)
                                .background(BorderSubtle)
                        )
                    },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = RoyalPurple,
                            height = 2.5.dp
                        )
                    }
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(0) }
                        },
                        text = {
                            Text(
                                text = "ACTIVITY FEED",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = if (pagerState.currentPage == 0) TextPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        },
                        text = {
                            Text(
                                text = "LEADERBOARD",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = if (pagerState.currentPage == 1) TextPrimary else TextMuted
                            )
                        }
                    )
                }

                // HorizontalPager between Feed and Leaderboard
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) { page ->
                    if (page == 0) {
                        FeedView(
                            feedEvents = uiState.feedEvents,
                            currentUserId = uiState.currentUserId,
                            onUserClick = onNavigateToPublicProfile,
                            onToggleReaction = onToggleReaction
                        )
                    } else {
                        GlobalLeaderboardView(
                            scope = uiState.leaderboardScope,
                            timeWindow = uiState.leaderboardWindow,
                            entries = uiState.leaderboardEntries,
                            currentUserId = uiState.currentUserId,
                            onScopeSelected = onScopeSelected,
                            onTimeWindowSelected = onTimeWindowSelected,
                            onUserClick = onNavigateToPublicProfile
                        )
                    }
                }
            }
        }

        // Friends Manager Bottom Sheet
        if (uiState.showFriendsSheet) {
            FriendsManagerSheet(
                friends = uiState.friends,
                pendingRequests = uiState.pendingRequests,
                searchQuery = uiState.searchQuery,
                searchResults = uiState.searchResults,
                isSearching = uiState.isSearching,
                onSearchQueryChanged = onSearchQueryChanged,
                onSendFriendRequest = onSendFriendRequest,
                onAcceptRequest = onAcceptRequest,
                onRejectRequest = onRejectRequest,
                onRemoveFriend = onRemoveFriend,
                onUserClick = { userId ->
                    onCloseFriendsSheet()
                    onNavigateToPublicProfile(userId)
                },
                onDismiss = onCloseFriendsSheet
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SocialScreenPreview() {
    QuestRingsTheme {
        SocialContent(
            uiState = SocialUiState(isLoading = false),
            onTabSelected = {},
            onScopeSelected = {},
            onTimeWindowSelected = {},
            onToggleReaction = { _, _ -> },
            onOpenFriendsSheet = {},
            onCloseFriendsSheet = {},
            onSearchQueryChanged = {},
            onSendFriendRequest = {},
            onAcceptRequest = { _, _ -> },
            onRejectRequest = {},
            onRemoveFriend = {},
            onNavigateToPublicProfile = {}
        )
    }
}
