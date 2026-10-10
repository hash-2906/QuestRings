package com.questrings.presentation.assignment.leaderboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.questrings.domain.model.assignment.RoomLeaderboardEntry

@Composable
fun RoomLeaderboardScreen(
    viewModel: RoomLeaderboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "ROOM LEADERBOARD",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.totalAssignmentsPosted} quests posted · ${uiState.totalCompetitors} members competed",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                if (uiState.entries.isEmpty()) {
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
                            Text(
                                text = "No room scores yet. Complete quests to take the lead!",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(uiState.entries, key = { it.userId }) { entry ->
                        val isRank1 = entry.rank == 1
                        val isRank2 = entry.rank == 2
                        val isRank3 = entry.rank == 3

                        val goldBorder = Brush.horizontalGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFFD700))
                        )
                        val silverBorder = Brush.horizontalGradient(
                            listOf(Color(0xFFE0E0E0), Color(0xFFA0A0A0), Color(0xFFE0E0E0))
                        )
                        val bronzeBorder = Brush.horizontalGradient(
                            listOf(Color(0xFFCD7F32), Color(0xFF8B4513), Color(0xFFCD7F32))
                        )

                        val borderModifier = when {
                            isRank1 -> Modifier.border(1.5.dp, goldBorder, RoundedCornerShape(16.dp))
                            isRank2 -> Modifier.border(1.dp, silverBorder, RoundedCornerShape(16.dp))
                            isRank3 -> Modifier.border(1.dp, bronzeBorder, RoundedCornerShape(16.dp))
                            entry.isCurrentUser -> Modifier.border(1.dp, AccentPurple, RoundedCornerShape(16.dp))
                            else -> Modifier.border(1.dp, BorderCard, RoundedCornerShape(16.dp))
                        }

                        val avatarColor = runCatching {
                            Color(android.graphics.Color.parseColor(entry.avatarColorHex))
                        }.getOrDefault(AccentPurple)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (entry.isCurrentUser) Color(0xFF24242B) else BackgroundCard)
                                .then(borderModifier)
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Animated Rank Number
                                Box(
                                    modifier = Modifier.width(36.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    AnimatedContent(
                                        targetState = entry.rank,
                                        transitionSpec = {
                                            slideInVertically { it } togetherWith slideOutVertically { -it }
                                        },
                                        label = "rankFlip"
                                    ) { rank ->
                                        Text(
                                            text = when (rank) {
                                                1 -> "👑 #1"
                                                2 -> "🥈 #2"
                                                3 -> "🥉 #3"
                                                else -> "#$rank"
                                            },
                                            fontSize = if (rank == 1) 15.sp else 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = when (rank) {
                                                1 -> Color(0xFFFFD700)
                                                2 -> Color(0xFFE0E0E0)
                                                3 -> Color(0xFFCD7F32)
                                                else -> TextMuted
                                            }
                                        )
                                    }
                                }

                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(avatarColor.copy(alpha = 0.25f))
                                        .border(1.dp, avatarColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = entry.displayName.firstOrNull()?.uppercase() ?: "A",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Name & Quests Count
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (entry.isCurrentUser) "${entry.displayName} (You)" else entry.displayName,
                                            color = if (entry.isCurrentUser) Color.White else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        val medalTier = com.questrings.domain.config.MedalProgressionConfig.evaluate(entry.totalXpEarned, 5)
                                        if (medalTier != com.questrings.domain.model.medal.MedalTier.NONE) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            com.questrings.presentation.medal.components.MedalBadge(tier = medalTier, size = 18.dp)
                                        }
                                    }
                                    Text(
                                        text = "${entry.assignmentsCompleted} quests · +${entry.totalXpEarned} XP",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Points
                                Text(
                                    text = "${entry.totalPoints} pts",
                                    color = if (isRank1) Color(0xFFFFD700) else AccentGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RoomLeaderboardScreenPreview() {
    QuestRingsTheme {
        RoomLeaderboardScreen()
    }
}
