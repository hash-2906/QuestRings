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
import com.questrings.presentation.tasks.TaskStatusFilter

@Composable
fun TaskStatusFilterChips(
    selectedFilter: TaskStatusFilter,
    onFilterSelected: (TaskStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(QuestTheme.colors.backgroundSecondary)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TaskStatusFilter.values().forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) QuestTheme.colors.backgroundCard
                        else Color.Transparent
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (filter) {
                        TaskStatusFilter.ALL -> "All"
                        TaskStatusFilter.ACTIVE -> "Active"
                        TaskStatusFilter.COMPLETED -> "Done"
                        TaskStatusFilter.FAILED -> "Failed"
                    },
                    style = QuestTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) QuestTheme.colors.textPrimary else QuestTheme.colors.textSecondary
                    )
                )
            }
        }
    }
}
