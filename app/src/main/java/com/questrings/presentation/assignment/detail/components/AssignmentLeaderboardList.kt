package com.questrings.presentation.assignment.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.LeaderboardEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AssignmentLeaderboardList(
    entries: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ASSIGNMENT LEADERBOARD (${entries.size})",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        if (entries.isEmpty()) {
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
                    Text(
                        text = "🏆 Be the first to claim Rank #1!",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete this quest early to lock in full speed bonus points.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            entries.forEach { entry ->
                val medal = when (entry.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "#${entry.rank}"
                }

                val avatarColor = runCatching {
                    Color(android.graphics.Color.parseColor(entry.avatarColorHex))
                }.getOrDefault(AccentPurple)

                val itemBg = if (entry.isCurrentUser) Color(0xFF26262D) else BackgroundCard
                val itemBorder = if (entry.isCurrentUser) AccentPurple else BorderCard

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(itemBg)
                        .border(1.dp, itemBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank / Medal Box
                        Box(
                            modifier = Modifier.width(36.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = medal,
                                fontSize = if (entry.rank <= 3) 18.sp else 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (entry.rank <= 3) TextPrimary else TextMuted
                            )
                        }

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.25f))
                                .border(1.dp, avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry.displayName.firstOrNull()?.uppercase() ?: "A",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Name + Time
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (entry.isCurrentUser) "${entry.displayName} (You)" else entry.displayName,
                                    color = if (entry.isCurrentUser) Color.White else TextPrimary,
                                    fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                val medalTier = com.questrings.domain.config.MedalProgressionConfig.evaluate(entry.pointsEarned * 15, 5)
                                if (medalTier != com.questrings.domain.model.medal.MedalTier.NONE) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    com.questrings.presentation.medal.components.MedalBadge(tier = medalTier, size = 16.dp)
                                }
                            }
                            Text(
                                text = "Finished ${timeFormat.format(Date(entry.completedAt))} · +${entry.xpAwarded} XP",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Points + Speed bonus tag
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${entry.pointsAwarded} pts",
                                color = if (entry.rank == 1) Color(0xFFFFD700) else AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            if (entry.speedBonusAwarded > 0) {
                                Text(
                                    text = "+${entry.speedBonusAwarded} speed",
                                    color = AccentPurple,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AssignmentLeaderboardListPreview() {
    QuestRingsTheme {
        AssignmentLeaderboardList(
            entries = listOf(
                LeaderboardEntry(
                    rank = 1,
                    userId = "user-1",
                    displayName = "ShadowKnight",
                    avatarColorHex = "#7E5DFF",
                    completedAt = System.currentTimeMillis() - 7200000L,
                    pointsAwarded = 125,
                    xpAwarded = 187,
                    isLate = false,
                    speedBonusAwarded = 25,
                    isCurrentUser = true
                ),
                LeaderboardEntry(
                    rank = 2,
                    userId = "user-2",
                    displayName = "PixelWizard",
                    avatarColorHex = "#30D158",
                    completedAt = System.currentTimeMillis() - 3600000L,
                    pointsAwarded = 100,
                    xpAwarded = 150,
                    isLate = false,
                    speedBonusAwarded = 15,
                    isCurrentUser = false
                )
            )
        )
    }
}
