package com.questrings.presentation.social.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.medal.MedalTier
import com.questrings.presentation.medal.components.MedalBadge

@Composable
fun LeaderboardList(
    entries: List<LeaderboardEntry>,
    currentUserId: String,
    onUserClick: (userId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No rankings recorded for this period.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            entries.forEach { entry ->
                LeaderboardRow(
                    entry = entry,
                    onClick = { onUserClick(entry.userId) }
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    entry: LeaderboardEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRank1 = entry.rank == 1
    val isRank2 = entry.rank == 2
    val isRank3 = entry.rank == 3

    val goldBorder = Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFFD700)))
    val silverBorder = Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFA0A0A0), Color(0xFFE0E0E0)))
    val bronzeBorder = Brush.horizontalGradient(listOf(Color(0xFFCD7F32), Color(0xFF8B4513), Color(0xFFCD7F32)))

    val borderModifier = when {
        isRank1 -> Modifier.border(1.5.dp, goldBorder, RoundedCornerShape(16.dp))
        isRank2 -> Modifier.border(1.dp, silverBorder, RoundedCornerShape(16.dp))
        isRank3 -> Modifier.border(1.dp, bronzeBorder, RoundedCornerShape(16.dp))
        entry.isCurrentUser -> Modifier.border(1.dp, RoyalPurple, RoundedCornerShape(16.dp))
        else -> Modifier.border(1.dp, BorderCard, RoundedCornerShape(16.dp))
    }

    val avatarColor = try {
        Color(android.graphics.Color.parseColor(entry.avatarColorHex))
    } catch (_: Exception) {
        RoyalPurple
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (entry.isCurrentUser) Color(0xFF201D2C) else BackgroundCard)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number or Medal
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = when (entry.rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#${entry.rank}"
                    },
                    fontSize = if (entry.rank <= 3) 17.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = when (entry.rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFE0E0E0)
                        3 -> Color(0xFFCD7F32)
                        else -> TextMuted
                    }
                )
            }

            // User Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(avatarColor)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Text(
                    text = entry.displayName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Display Name + Medal
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (entry.isCurrentUser) "${entry.displayName} (You)" else entry.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val medalTier = MedalProgressionConfig.evaluate(entry.xpAwarded, 1)
                    if (medalTier != MedalTier.NONE) {
                        Spacer(modifier = Modifier.width(6.dp))
                        MedalBadge(tier = medalTier, size = 16.dp)
                    }
                }
            }

            // XP Score
            Text(
                text = "${"%,d".format(entry.xpAwarded)} XP",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = DisciplineBlue
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun LeaderboardListPreview() {
    QuestRingsTheme {
        LeaderboardList(
            entries = listOf(
                LeaderboardEntry(
                    rank = 1,
                    userId = "u1",
                    displayName = "ShadowMaster",
                    avatarColorHex = "#FF2D55",
                    completedAt = 0L,
                    pointsAwarded = 0,
                    xpAwarded = 14200,
                    isLate = false,
                    speedBonusAwarded = 0,
                    isCurrentUser = false
                ),
                LeaderboardEntry(
                    rank = 2,
                    userId = "me",
                    displayName = "CyberKnight",
                    avatarColorHex = "#7E5DFF",
                    completedAt = 0L,
                    pointsAwarded = 0,
                    xpAwarded = 11450,
                    isLate = false,
                    speedBonusAwarded = 0,
                    isCurrentUser = true
                )
            ),
            currentUserId = "me",
            onUserClick = {}
        )
    }
}
