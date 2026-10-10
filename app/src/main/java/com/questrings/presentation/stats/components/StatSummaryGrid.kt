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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestTheme

@Composable
fun StatSummaryGrid(
    currentStreak: Int,
    longestStreak: Int,
    totalCompleted: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "CURRENT STREAK",
                value = "$currentStreak days",
                icon = Icons.Default.Whatshot,
                tint = QuestTheme.colors.streakOrange,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "LONGEST STREAK",
                value = "$longestStreak days",
                icon = Icons.Default.MilitaryTech,
                tint = Color(0xFFFFD60A),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "QUESTS COMPLETED",
                value = "$totalCompleted",
                icon = Icons.Default.CheckCircle,
                tint = QuestTheme.colors.intellectGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "RANK TIER",
                value = "Level $level",
                icon = Icons.Default.ElectricBolt,
                tint = QuestTheme.colors.levelPurple,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    QuestCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 10.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = QuestTheme.typography.monoNumber.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuestTheme.colors.textPrimary
                )
            )
        }
    }
}
