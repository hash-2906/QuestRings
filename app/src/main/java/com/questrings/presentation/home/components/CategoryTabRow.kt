package com.questrings.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.HabitCategory

@Composable
fun CategoryTabRow(
    selectedCategory: HabitCategory?,
    onCategorySelected: (HabitCategory?) -> Unit,
    vitalityCount: Pair<Int, Int>,
    intellectCount: Pair<Int, Int>,
    disciplineCount: Pair<Int, Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(QuestTheme.colors.backgroundSecondary)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CategoryTab(
            title = "All",
            count = null,
            isSelected = selectedCategory == null,
            activeColor = QuestTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
            onClick = { onCategorySelected(null) }
        )

        CategoryTab(
            title = "Vitality",
            count = "${vitalityCount.first}/${vitalityCount.second}",
            isSelected = selectedCategory == HabitCategory.VITALITY,
            activeColor = QuestTheme.colors.vitalityRed,
            modifier = Modifier.weight(1.2f),
            onClick = { onCategorySelected(HabitCategory.VITALITY) }
        )

        CategoryTab(
            title = "Intellect",
            count = "${intellectCount.first}/${intellectCount.second}",
            isSelected = selectedCategory == HabitCategory.INTELLECT,
            activeColor = QuestTheme.colors.intellectGreen,
            modifier = Modifier.weight(1.2f),
            onClick = { onCategorySelected(HabitCategory.INTELLECT) }
        )

        CategoryTab(
            title = "Discipline",
            count = "${disciplineCount.first}/${disciplineCount.second}",
            isSelected = selectedCategory == HabitCategory.DISCIPLINE,
            activeColor = QuestTheme.colors.disciplineBlue,
            modifier = Modifier.weight(1.2f),
            onClick = { onCategorySelected(HabitCategory.DISCIPLINE) }
        )
    }
}

@Composable
private fun CategoryTab(
    title: String,
    count: String?,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) QuestTheme.colors.backgroundCard else Color.Transparent,
        label = "tabBg"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = QuestTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) activeColor else QuestTheme.colors.textSecondary
                )
            )
            if (count != null) {
                Text(
                    text = " $count",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 10.sp,
                        color = if (isSelected) activeColor.copy(alpha = 0.8f) else QuestTheme.colors.textMuted
                    )
                )
            }
        }
    }
}
