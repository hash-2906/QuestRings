package com.questrings.presentation.rooms.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.components.QuestBottomBar
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.components.QuestNavigationTab
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.room.RoomRole
import com.questrings.presentation.rooms.list.components.CreateRoomSheet
import com.questrings.presentation.rooms.list.components.RoomCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    viewModel: RoomsViewModel,
    onNavigateToRoom: (String) -> Unit,
    onNavigateToShare: (String) -> Unit,
    onNavigateToJoin: () -> Unit,
    onNavigateToRings: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToSocial: () -> Unit = {},
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Navigation Effect collector
    LaunchedEffect(Unit) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is RoomsNavigationEffect.NavigateToRoom -> onNavigateToRoom(effect.roomId)
                is RoomsNavigationEffect.NavigateToShare -> onNavigateToShare(effect.roomId)
                is RoomsNavigationEffect.NavigateToJoin -> onNavigateToJoin()
            }
        }
    }

    // Auto-dismiss toast
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(2500)
            viewModel.onEvent(RoomsUiEvent.DismissToast)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuestTheme.colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "QUEST ROOMS",
                            style = QuestTheme.typography.heroDisplay.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestTheme.colors.textPrimary
                            )
                        )
                        Text(
                            text = "Collaborate & conquer together.",
                            style = QuestTheme.typography.bodySmall.copy(
                                color = QuestTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                actions = {
                    // Join Room Button
                    IconButton(onClick = onNavigateToJoin) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = "Join Room",
                            tint = QuestTheme.colors.disciplineBlue
                        )
                    }

                    // Create Room Button
                    IconButton(onClick = { viewModel.onEvent(RoomsUiEvent.OpenCreateRoomSheet) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Room",
                            tint = QuestTheme.colors.vitalityRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuestTheme.colors.backgroundPrimary
                )
            )
        },
        bottomBar = {
            QuestBottomBar(
                currentTab = QuestNavigationTab.Rooms,
                onTabSelected = { tab ->
                    when (tab) {
                        QuestNavigationTab.Rings -> onNavigateToRings()
                        QuestNavigationTab.Tasks -> onNavigateToTasks()
                        QuestNavigationTab.Social -> onNavigateToSocial()
                        QuestNavigationTab.Profile -> onNavigateToStats()
                        QuestNavigationTab.Rooms -> {}
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab Row
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = QuestTheme.colors.backgroundPrimary,
                    contentColor = QuestTheme.colors.textPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = QuestTheme.colors.vitalityRed,
                            height = 2.dp
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                        text = {
                            Text(
                                text = "MY ROOMS (${uiState.myRooms.size})",
                                style = QuestTheme.typography.monoLabel.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (pagerState.currentPage == 0) QuestTheme.colors.textPrimary else QuestTheme.colors.textMuted
                                )
                            )
                        }
                    )

                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                        text = {
                            Text(
                                text = "JOINED ROOMS (${uiState.joinedRooms.size})",
                                style = QuestTheme.typography.monoLabel.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (pagerState.currentPage == 1) QuestTheme.colors.textPrimary else QuestTheme.colors.textMuted
                                )
                            )
                        }
                    )
                }

                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val rooms = if (page == 0) uiState.myRooms else uiState.joinedRooms

                    if (uiState.isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(3) {
                                com.questrings.core.designsystem.components.skeletons.RoomCardSkeleton()
                            }
                        }
                    } else if (rooms.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            com.questrings.core.designsystem.components.QuestEmptyState(
                                iconEmoji = if (page == 0) "🛡️" else "🗝️",
                                title = if (page == 0) "No Created Chambers" else "No Chambers Joined",
                                description = if (page == 0)
                                    "Forge a private syndicate or public hall to challenge teammates together."
                                else
                                    "Enter a 6-character room code or scan a QR code to join your party.",
                                actionLabel = if (page == 0) "+ Create Room" else "Join Room with Code",
                                onActionClick = {
                                    if (page == 0) viewModel.onEvent(RoomsUiEvent.OpenCreateRoomSheet)
                                    else onNavigateToJoin()
                                }
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(rooms, key = { it.id }) { room ->
                                RoomCard(
                                    room = room,
                                    userRole = if (page == 0) RoomRole.ADMIN else RoomRole.MEMBER,
                                    onClick = { onNavigateToRoom(room.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Toast feedback popup
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                uiState.toastMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(QuestTheme.colors.backgroundCard)
                            .border(1.dp, QuestTheme.colors.streakOrange.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = msg,
                            style = QuestTheme.typography.bodyMedium.copy(
                                color = QuestTheme.colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    if (uiState.isCreateRoomSheetOpen) {
        CreateRoomSheet(
            sheetState = sheetState,
            onDismiss = { viewModel.onEvent(RoomsUiEvent.CloseCreateRoomSheet) },
            onCreate = { name, desc, emoji, privacy, maxMembers ->
                viewModel.createRoom(name, desc, emoji, privacy, maxMembers)
            }
        )
    }
}
