package com.questrings.presentation.stats.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.HabitCategory

@Composable
fun XpByCategoryChart(
    xpByCategory: Map<HabitCategory, Int>,
    modifier: Modifier = Modifier
) {
    val totalXp = xpByCategory.values.sum().coerceAtLeast(1)

    QuestCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "XP BREAKDOWN BY CATEGORY",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HabitCategory.values().forEach { category ->
                    val xp = xpByCategory[category] ?: 0
                    val fraction = (xp.toFloat() / totalXp.toFloat()).coerceIn(0f, 1f)
                    val color = when (category) {
                        HabitCategory.VITALITY -> QuestTheme.colors.vitalityRed
                        HabitCategory.INTELLECT -> QuestTheme.colors.intellectGreen
                        HabitCategory.DISCIPLINE -> QuestTheme.colors.disciplineBlue
                    }

                    CategoryBarItem(
                        name = category.displayName,
                        xp = xp,
                        fraction = fraction,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBarItem(
    name: String,
    xp: Int,
    fraction: Float,
    color: Color
) {
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "barAnim")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = QuestTheme.typography.labelMedium.copy(
                    color = QuestTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "$xp XP (${(fraction * 100).toInt()}%)",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textSecondary
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(QuestTheme.colors.backgroundSecondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
            )
        }
    }
}
