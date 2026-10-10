package com.questrings.presentation.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.TaskPriority

@Composable
fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskPriority.values().forEach { priority ->
            val isSelected = priority == selectedPriority
            val color = when (priority) {
                TaskPriority.LOW -> QuestTheme.colors.intellectGreen
                TaskPriority.MEDIUM -> QuestTheme.colors.disciplineBlue
                TaskPriority.HIGH -> QuestTheme.colors.streakOrange
                TaskPriority.URGENT -> QuestTheme.colors.vitalityRed
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) color.copy(alpha = 0.2f)
                        else QuestTheme.colors.backgroundSecondary
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) color else QuestTheme.colors.borderSubtle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onPrioritySelected(priority) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = priority.displayName.uppercase(),
                    style = QuestTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) color else QuestTheme.colors.textSecondary
                    )
                )
            }
        }
    }
}
