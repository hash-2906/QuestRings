package com.questrings.presentation.profile.public

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.social.FriendshipStatus
import com.questrings.domain.model.social.PublicProfile
import com.questrings.presentation.medal.components.MedalBadge
import com.questrings.presentation.profile.public.components.MutualRoomsList
import com.questrings.presentation.profile.public.components.ProfileStatsStrip
import com.questrings.presentation.profile.public.components.RingFocusBreakdown

@Composable
fun PublicProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: PublicProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PublicProfileContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSendFriendRequest = viewModel::sendFriendRequest,
        onRemoveFriend = viewModel::removeFriend
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileContent(
    uiState: PublicProfileUiState,
    onNavigateBack: () -> Unit,
    onSendFriendRequest: () -> Unit,
    onRemoveFriend: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PLAYER DOSSIER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
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
        } else if (uiState.profile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Player dossier unavailable",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        } else {
            val profile = uiState.profile
            val avatarColor = try {
                Color(android.graphics.Color.parseColor(profile.avatarColorHex))
            } catch (_: Exception) {
                RoyalPurple
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Large Avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                            .border(2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    ) {
                        Text(
                            text = profile.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (profile.currentMedal != MedalTier.NONE) {
                        MedalBadge(
                            tier = profile.currentMedal,
                            showLabel = true,
                            size = 24.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Friendship Action Button
                    when (profile.friendshipStatus) {
                        FriendshipStatus.SELF -> {
                            // Viewing own profile: no action needed
                        }
                        FriendshipStatus.FRIENDS -> {
                            OutlinedButton(
                                onClick = onRemoveFriend,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IntellectGreen.copy(alpha = 0.8f)),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text(
                                    text = "✓ Friends",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = IntellectGreen
                                )
                            }
                        }
                        FriendshipStatus.PENDING_SENT -> {
                            OutlinedButton(
                                onClick = {},
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextMuted),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text(
                                    text = "Request Pending ⏳",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = TextMuted
                                )
                            }
                        }
                        FriendshipStatus.PENDING_RECEIVED -> {
                            Button(
                                onClick = onSendFriendRequest,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IntellectGreen),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text(
                                    text = "✓ Accept Request",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                            }
                        }
                        FriendshipStatus.NONE -> {
                            Button(
                                onClick = onSendFriendRequest,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text(
                                    text = "+ Add Friend",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Stats Strip
                item {
                    ProfileStatsStrip(
                        level = profile.level,
                        streak = profile.currentStreak,
                        totalXp = profile.totalXp
                    )
                }

                // Focus Breakdown
                item {
                    RingFocusBreakdown(xpByCategory = profile.xpByCategory)
                }

                // Recent Achievements
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BackgroundCard)
                            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "RECENT ACHIEVEMENTS (${profile.recentAchievements.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = TextMuted
                            )

                            if (profile.recentAchievements.isEmpty()) {
                                Text(
                                    text = "No recorded achievements yet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            } else {
                                profile.recentAchievements.forEach { event ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🏆 ${event.description}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = TextPrimary
                                        )
                                        if (event.xpEarned > 0) {
                                            Text(
                                                text = "+${event.xpEarned} XP",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = RoyalPurple
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Mutual Rooms
                item {
                    MutualRoomsList(mutualRooms = profile.mutualRooms)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicProfileScreenPreview() {
    QuestRingsTheme {
        PublicProfileContent(
            uiState = PublicProfileUiState(
                profile = PublicProfile(
                    userId = "u1",
                    displayName = "Riya",
                    avatarColorHex = "#FF2D55",
                    level = 24,
                    currentMedal = MedalTier.GOLD,
                    currentStreak = 12,
                    totalXp = 12450,
                    xpByCategory = mapOf(
                        HabitCategory.VITALITY to 6225,
                        HabitCategory.INTELLECT to 3735,
                        HabitCategory.DISCIPLINE to 2490
                    ),
                    friendshipStatus = FriendshipStatus.FRIENDS
                ),
                isLoading = false
            ),
            onNavigateBack = {},
            onSendFriendRequest = {},
            onRemoveFriend = {}
        )
    }
}
