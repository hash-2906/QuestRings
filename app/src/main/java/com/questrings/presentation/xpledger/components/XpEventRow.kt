package com.questrings.presentation.xpledger.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun XpEventRow(
    event: XpEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (sourceIcon, sourceColor) = when (event.source) {
        XpSource.HABIT_COMPLETION -> "⚡" to VitalityRed
        XpSource.RING_CLOSED -> "⭕" to VitalityRed
        XpSource.ALL_RINGS_CLOSED -> "👑" to RoyalPurple
        XpSource.TASK_COMPLETION -> "⚔️" to IntellectGreen
        XpSource.ROOM_ASSIGNMENT_COMPLETION -> "🏆" to DisciplineBlue
        XpSource.STREAK_MILESTONE -> "🔥" to StreakOrange
        XpSource.LEVEL_UP_BONUS -> "⭐" to Color(0xFFFFD700)
    }

    val timeFormatted = formatTimeAgo(event.earnedAt)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(sourceColor.copy(alpha = 0.15f))
                .border(1.dp, sourceColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
        ) {
            Text(text = sourceIcon, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and sub info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.description.ifBlank { event.sourceEntityTitle ?: event.source.displayName },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.source.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = " • $timeFormatted",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // XP Gain badge & multiplier tag
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${event.totalXp} XP",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (event.bonusXp > 0) StreakOrange else DisciplineBlue
            )
            if (event.multiplier > 1.0f) {
                Text(
                    text = "×${"%.2f".format(event.multiplier).trimEnd('0').trimEnd('.')} streak",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = StreakOrange
                )
            }
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
fun XpEventRowPreview() {
    QuestRingsTheme {
        XpEventRow(
            event = XpEvent(
                id = "1",
                userId = "u1",
                source = XpSource.TASK_COMPLETION,
                baseXp = 100,
                bonusXp = 25,
                totalXp = 125,
                multiplier = 1.25f,
                description = "Complete Clean Architecture refactor",
                sourceEntityId = "t1",
                sourceEntityTitle = "Clean Architecture",
                earnedAt = System.currentTimeMillis() - 3600000,
                cumulativeXpAfter = 1420
            ),
            onClick = {}
        )
    }
}
