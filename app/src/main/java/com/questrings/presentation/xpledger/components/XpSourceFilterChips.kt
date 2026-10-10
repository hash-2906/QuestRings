package com.questrings.presentation.xpledger.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.domain.repository.XpSourceFilter

@Composable
fun XpSourceFilterChips(
    selectedFilter: XpSourceFilter,
    onFilterSelected: (XpSourceFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        XpSourceFilter.ALL to "ALL SOURCES",
        XpSourceFilter.HABITS to "HABITS & RINGS",
        XpSourceFilter.TASKS to "QUESTS",
        XpSourceFilter.ROOMS to "ROOM ASSIGNMENTS"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { (filter, label) ->
            val isSelected = filter == selectedFilter
            val bgColor = if (isSelected) RoyalPurple.copy(alpha = 0.2f) else BackgroundCard
            val borderColor = if (isSelected) RoyalPurple else BorderCard
            val textColor = if (isSelected) TextPrimary else TextMuted

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun XpSourceFilterChipsPreview() {
    QuestRingsTheme {
        XpSourceFilterChips(
            selectedFilter = XpSourceFilter.ALL,
            onFilterSelected = {}
        )
    }
}
