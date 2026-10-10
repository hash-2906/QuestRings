package com.questrings.presentation.social.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow

@Composable
fun LeaderboardFilters(
    currentScope: LeaderboardScope,
    currentTimeWindow: LeaderboardTimeWindow,
    onScopeSelected: (LeaderboardScope) -> Unit,
    onTimeWindowSelected: (LeaderboardTimeWindow) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segmented Scope Toggle [ Friends | Global ]
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(BackgroundCard)
                    .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
                    .padding(3.dp)
            ) {
                Row {
                    ScopeButton(
                        title = "Friends",
                        isSelected = currentScope == LeaderboardScope.FRIENDS,
                        onClick = { onScopeSelected(LeaderboardScope.FRIENDS) }
                    )
                    ScopeButton(
                        title = "Global",
                        isSelected = currentScope == LeaderboardScope.GLOBAL,
                        onClick = { onScopeSelected(LeaderboardScope.GLOBAL) }
                    )
                }
            }

            // Time Window Dropdown
            Box {
                val windowLabel = when (currentTimeWindow) {
                    LeaderboardTimeWindow.WEEKLY -> "This Week ▾"
                    LeaderboardTimeWindow.MONTHLY -> "This Month ▾"
                    LeaderboardTimeWindow.ALL_TIME -> "All Time ▾"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundCard)
                        .border(1.dp, BorderCard, RoundedCornerShape(12.dp))
                        .clickable { isDropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = windowLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.background(BackgroundCard)
                ) {
                    DropdownMenuItem(
                        text = { Text("This Week", color = TextPrimary) },
                        onClick = {
                            onTimeWindowSelected(LeaderboardTimeWindow.WEEKLY)
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("This Month", color = TextPrimary) },
                        onClick = {
                            onTimeWindowSelected(LeaderboardTimeWindow.MONTHLY)
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("All Time", color = TextPrimary) },
                        onClick = {
                            onTimeWindowSelected(LeaderboardTimeWindow.ALL_TIME)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScopeButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) RoyalPurple else Color.Transparent
    val textColor = if (isSelected) Color.White else TextMuted

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun LeaderboardFiltersPreview() {
    QuestRingsTheme {
        LeaderboardFilters(
            currentScope = LeaderboardScope.FRIENDS,
            currentTimeWindow = LeaderboardTimeWindow.WEEKLY,
            onScopeSelected = {},
            onTimeWindowSelected = {}
        )
    }
}
