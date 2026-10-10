package com.questrings.presentation.social.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.domain.model.assignment.LeaderboardEntry
import com.questrings.domain.model.social.LeaderboardScope
import com.questrings.domain.model.social.LeaderboardTimeWindow

@Composable
fun GlobalLeaderboardView(
    scope: LeaderboardScope,
    timeWindow: LeaderboardTimeWindow,
    entries: List<LeaderboardEntry>,
    currentUserId: String,
    onScopeSelected: (LeaderboardScope) -> Unit,
    onTimeWindowSelected: (LeaderboardTimeWindow) -> Unit,
    onUserClick: (userId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            LeaderboardFilters(
                currentScope = scope,
                currentTimeWindow = timeWindow,
                onScopeSelected = onScopeSelected,
                onTimeWindowSelected = onTimeWindowSelected
            )
        }

        item {
            LeaderboardList(
                entries = entries,
                currentUserId = currentUserId,
                onUserClick = onUserClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GlobalLeaderboardViewPreview() {
    QuestRingsTheme {
        GlobalLeaderboardView(
            scope = LeaderboardScope.FRIENDS,
            timeWindow = LeaderboardTimeWindow.WEEKLY,
            entries = emptyList(),
            currentUserId = "me",
            onScopeSelected = {},
            onTimeWindowSelected = {},
            onUserClick = {}
        )
    }
}
