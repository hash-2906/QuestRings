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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.DailyProgress

@Composable
fun MonthlyHeatmap(
    history: List<DailyProgress>,
    modifier: Modifier = Modifier
) {
    QuestCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVITY HEATMAP (LAST 90 DAYS)",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Less",
                        style = QuestTheme.typography.monoLabel.copy(fontSize = 9.sp, color = QuestTheme.colors.textMuted)
                    )
                    HeatmapBox(level = 0)
                    HeatmapBox(level = 1)
                    HeatmapBox(level = 2)
                    HeatmapBox(level = 3)
                    HeatmapBox(level = 4)
                    Text(
                        text = "More",
                        style = QuestTheme.typography.monoLabel.copy(fontSize = 9.sp, color = QuestTheme.colors.textMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 13 columns of 7 days (representing ~91 days)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(13) { colIdx ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(7) { rowIdx ->
                            val itemIdx = colIdx * 7 + rowIdx
                            val progress = if (itemIdx < history.size) history[itemIdx] else null
                            val level = when {
                                progress == null || progress.completionPercent == 0 -> 0
                                progress.completionPercent < 35 -> 1
                                progress.completionPercent < 70 -> 2
                                progress.completionPercent < 100 -> 3
                                else -> 4
                            }
                            HeatmapBox(level = level)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapBox(level: Int) {
    val color = when (level) {
        1 -> QuestTheme.colors.intellectGreen.copy(alpha = 0.25f)
        2 -> QuestTheme.colors.intellectGreen.copy(alpha = 0.5f)
        3 -> QuestTheme.colors.intellectGreen.copy(alpha = 0.75f)
        4 -> QuestTheme.colors.intellectGreen
        else -> QuestTheme.colors.backgroundSecondary
    }

    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
            .border(0.5.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(3.dp))
    )
}
