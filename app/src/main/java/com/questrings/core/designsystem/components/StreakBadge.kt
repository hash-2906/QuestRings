package com.questrings.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.StreakGlowBg
import com.questrings.core.designsystem.theme.StreakGlowBorder
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    subtitle: String = "Keep the fire alive"
) {
    if (isCompact) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(StreakGlowBg)
                .border(1.dp, StreakGlowBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak Fire",
                tint = StreakOrange,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "$streakDays",
                style = QuestTheme.typography.monoCounter,
                color = StreakOrange,
                fontSize = 11.sp
            )
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(41.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(StreakGlowBg)
                    .border(1.dp, StreakGlowBorder, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak Fire",
                    tint = StreakOrange,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "$streakDays day streak",
                    style = QuestTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = QuestTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
