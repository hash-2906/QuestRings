package com.questrings.presentation.social.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.social.FeedEventType
import com.questrings.domain.model.social.SocialFeedEvent

@Composable
fun SocialFeedCard(
    event: SocialFeedEvent,
    currentUserId: String,
    onUserClick: (userId: String) -> Unit,
    onToggleReaction: (emoji: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarColor = try {
        Color(android.graphics.Color.parseColor(event.avatarColorHex))
    } catch (_: Exception) {
        RoyalPurple
    }

    val timeFormatted = formatTimeAgo(event.timestamp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onUserClick(event.userId) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                            .border(1.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Text(
                            text = event.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = event.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                // XP Gain Pill
                if (event.xpEarned > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DisciplineBlue.copy(alpha = 0.15f))
                            .border(1.dp, DisciplineBlue.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+${"%,d".format(event.xpEarned)} XP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = DisciplineBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reactions Row
            ReactionRow(
                reactions = event.reactions,
                currentUserId = currentUserId,
                onToggleReaction = onToggleReaction
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "Just now"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SocialFeedCardPreview() {
    QuestRingsTheme {
        SocialFeedCard(
            event = SocialFeedEvent(
                id = "1",
                userId = "u1",
                displayName = "Riya",
                avatarColorHex = "#FF2D55",
                eventType = FeedEventType.MEDAL_UNLOCKED,
                description = "unlocked Gold Medal 🏆",
                timestamp = System.currentTimeMillis() - 7200000,
                xpEarned = 7500,
                reactions = mapOf("👏" to listOf("u2"), "🔥" to listOf("me"))
            ),
            currentUserId = "me",
            onUserClick = {},
            onToggleReaction = {}
        )
    }
}
