package com.questrings.presentation.stats.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestTheme

@Composable
fun LongestStreakBadge(
    longestStreak: Int,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    QuestCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFF8B35), Color(0xFFFF2D55))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LIFETIME RECORD",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 10.sp,
                        color = QuestTheme.colors.streakOrange,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$longestStreak Day Legendary Streak",
                    style = QuestTheme.typography.headingMedium.copy(
                        fontSize = 16.sp,
                        color = QuestTheme.colors.textPrimary
                    )
                )
                Text(
                    text = if (currentStreak >= longestStreak) "You are currently at your peak!"
                           else "${longestStreak - currentStreak} days to break your personal record",
                    style = QuestTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = QuestTheme.colors.textSecondary
                    )
                )
            }
        }
    }
}
