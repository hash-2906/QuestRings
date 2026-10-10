package com.questrings.presentation.rooms.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomPermissions
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.presentation.assignment.leaderboard.RoomLeaderboardScreen
import com.questrings.presentation.assignment.list.components.AssignmentCard
import com.questrings.presentation.assignment.list.components.CreateAssignmentSheet
import com.questrings.presentation.assignment.verification.VerificationQueueScreen
import com.questrings.presentation.rooms.detail.components.JoinRequestCard
import com.questrings.presentation.rooms.detail.components.MemberRow
import com.questrings.presentation.rooms.detail.components.RoomHeaderCard
import com.questrings.presentation.rooms.detail.components.RoomSettingsSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToShare: (String) -> Unit,
    onNavigateToAssignmentDetail: (roomId: String, assignmentId: String) -> Unit = { _, _ -> },
    viewModel: RoomDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val createAssignmentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var memberToRemove by remember { mutableStateOf<RoomMember?>(null) }
    var showLeaveConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                RoomDetailNavigationEffect.NavigateBack -> onNavigateBack()
                is RoomDetailNavigationEffect.NavigateToShare -> onNavigateToShare(effect.roomId)
                is RoomDetailNavigationEffect.NavigateToAssignmentDetail ->
                    onNavigateToAssignmentDetail(effect.roomId, effect.assignmentId)
            }
        }
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    val canPostQuests = RoomPermissions.canCreateAssignment(uiState.currentUserRole)
    val canModerate = RoomPermissions.canApproveRequests(uiState.currentUserRole)

    Scaffold(
        containerColor = BackgroundPrimary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (canPostQuests && uiState.selectedTab == RoomDetailTab.ASSIGNMENTS) {
                FloatingActionButton(
                    onClick = { viewModel.setCreateAssignmentSheetOpen(true) },
                    containerColor = AccentPurple,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Post Quest",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = uiState.room?.name ?: "QUEST ROOM",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                uiState.room?.let { room ->
                    IconButton(onClick = { onNavigateToShare(room.id) }) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Share Room",
                            tint = AccentPurple
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading || uiState.room == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentPurple)
            }
        } else {
            val room = uiState.room!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header Card
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    RoomHeaderCard(
                        room = room,
                        userRole = uiState.currentUserRole,
                        onOpenShare = { onNavigateToShare(room.id) },
                        onOpenSettings = { viewModel.setSettingsOpen(true) }
                    )
                }

                // Tab Row (Scrollable to comfortably hold ASSIGNMENTS, MEMBERS, LEADERBOARD, REQUESTS, VERIFY)
                val tabs = buildList {
                    add(RoomDetailTab.ASSIGNMENTS)
                    add(RoomDetailTab.MEMBERS)
                    add(RoomDetailTab.LEADERBOARD)
                    if (canModerate) {
                        add(RoomDetailTab.REQUESTS)
                        add(RoomDetailTab.VERIFY)
                    }
                }

                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0),
                    containerColor = BackgroundPrimary,
                    contentColor = AccentPurple,
                    edgePadding = 20.dp,
                    indicator = { tabPositions ->
                        val index = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)
                        if (index < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = AccentPurple
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEach { tab ->
                        val selected = uiState.selectedTab == tab
                        Tab(
                            selected = selected,
                            onClick = { viewModel.selectTab(tab) },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = tab.title,
                                        color = if (selected) AccentPurple else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (tab == RoomDetailTab.REQUESTS && uiState.pendingRequests.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF2D55)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${uiState.pendingRequests.size}",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    if (tab == RoomDetailTab.VERIFY && uiState.pendingVerifications.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(AccentPurple),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${uiState.pendingVerifications.size}",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tab Content Switcher
                when (uiState.selectedTab) {
                    RoomDetailTab.ASSIGNMENTS -> {
                        val activeQuests = uiState.assignments.filter { it.liveStatus != AssignmentStatus.CLOSED }
                        val closedQuests = uiState.assignments.filter { it.liveStatus == AssignmentStatus.CLOSED }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Text(
                                    text = "ACTIVE QUESTS (${activeQuests.size})",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (activeQuests.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(BackgroundCard)
                                            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = null,
                                                tint = AccentPurple,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "NO ACTIVE QUESTS",
                                                color = TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (canPostQuests) "Tap the + button below to post a party quest." else "Chamber leaders will post new challenges soon.",
                                                color = TextMuted,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(activeQuests, key = { it.assignment.id }) { item ->
                                    AssignmentCard(
                                        assignmentWithStatus = item,
                                        onClick = { viewModel.openAssignment(item.assignment.id) },
                                        onDelete = { viewModel.deleteAssignment(item.assignment.id) },
                                        canDelete = uiState.currentUserRole == com.questrings.domain.model.room.RoomRole.ADMIN
                                    )
                                }
                            }

                            // Closed / Completed Quests (Collapsible)
                            if (closedQuests.isNotEmpty()) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.toggleClosedAssignmentsCollapsed() }
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "COMPLETED QUESTS (${closedQuests.size})",
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Icon(
                                            imageVector = if (uiState.isClosedAssignmentsCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                            contentDescription = "Toggle closed",
                                            tint = TextMuted
                                        )
                                    }
                                }

                                if (!uiState.isClosedAssignmentsCollapsed) {
                                    items(closedQuests, key = { it.assignment.id }) { item ->
                                        AssignmentCard(
                                            assignmentWithStatus = item,
                                            onClick = { viewModel.openAssignment(item.assignment.id) },
                                            onDelete = { viewModel.deleteAssignment(item.assignment.id) },
                                            canDelete = uiState.currentUserRole == com.questrings.domain.model.room.RoomRole.ADMIN
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }

                    RoomDetailTab.MEMBERS -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Text(
                                    text = "ROSTER (${uiState.members.size})",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            items(uiState.members, key = { it.userId }) { member ->
                                MemberRow(
                                    member = member,
                                    currentUserRole = uiState.currentUserRole,
                                    currentUserId = uiState.currentUserId,
                                    onPromoteRole = { newRole ->
                                        viewModel.updateMemberRole(member.userId, newRole)
                                    },
                                    onRemoveMember = {
                                        memberToRemove = member
                                    },
                                    onLeaveRoom = {
                                        showLeaveConfirm = true
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }

                    RoomDetailTab.LEADERBOARD -> {
                        RoomLeaderboardScreen()
                    }

                    RoomDetailTab.REQUESTS -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (uiState.pendingRequests.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(BackgroundCard)
                                            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
                                            .padding(28.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = AccentGreen,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "NO PENDING REQUESTS",
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(uiState.pendingRequests, key = { it.id }) { request ->
                                    JoinRequestCard(
                                        request = request,
                                        onApprove = { viewModel.approveRequest(request) },
                                        onReject = { viewModel.rejectRequest(request) }
                                    )
                                }
                            }
                        }
                    }

                    RoomDetailTab.VERIFY -> {
                        VerificationQueueScreen()
                    }
                }
            }
        }

        // Create Assignment Sheet
        if (uiState.isCreateAssignmentSheetOpen && uiState.room != null) {
            CreateAssignmentSheet(
                sheetState = createAssignmentSheetState,
                requiresVerification = uiState.room?.privacy == RoomPrivacy.PRIVATE,
                onDismiss = {
                    scope.launch { createAssignmentSheetState.hide() }.invokeOnCompletion {
                        viewModel.setCreateAssignmentSheetOpen(false)
                    }
                },
                onSubmit = { title, desc, type, items, dueAt ->
                    viewModel.createAssignment(title, desc, type, items, dueAt)
                }
            )
        }

        // Room Settings Sheet
        if (uiState.isSettingsOpen && uiState.room != null) {
            RoomSettingsSheet(
                room = uiState.room!!,
                userRole = uiState.currentUserRole,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        viewModel.setSettingsOpen(false)
                    }
                },
                onSaveSettings = { name, desc, privacy ->
                    viewModel.saveSettings(name, desc, privacy)
                },
                onRegenerateCode = {
                    viewModel.regenerateCode()
                },
                onDeleteRoom = {
                    viewModel.deleteRoom()
                }
            )
        }

        // Remove Member Alert Dialog
        memberToRemove?.let { member ->
            AlertDialog(
                onDismissRequest = { memberToRemove = null },
                containerColor = BackgroundCard,
                title = {
                    Text(
                        text = "Remove Member?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to remove ${member.displayName} from this room? They will lose access to active party assignments.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.removeMember(member.userId)
                            memberToRemove = null
                        }
                    ) {
                        Text("REMOVE", color = Color(0xFFFF2D55), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { memberToRemove = null }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }

        // Leave Room Alert Dialog
        if (showLeaveConfirm) {
            AlertDialog(
                onDismissRequest = { showLeaveConfirm = false },
                containerColor = BackgroundCard,
                title = {
                    Text(
                        text = "Leave Room?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to leave ${uiState.room?.name}? If this is a private room, you will need approval to rejoin.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLeaveConfirm = false
                            viewModel.leaveRoom()
                        }
                    ) {
                        Text("LEAVE", color = Color(0xFFFF2D55), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveConfirm = false }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RoomDetailScreenPreview() {
    QuestRingsTheme {
        RoomDetailScreen(
            onNavigateBack = {},
            onNavigateToShare = {}
        )
    }
}
