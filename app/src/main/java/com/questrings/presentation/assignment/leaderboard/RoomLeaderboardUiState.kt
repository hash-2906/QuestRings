package com.questrings.presentation.assignment.leaderboard

import com.questrings.domain.model.assignment.RoomLeaderboardEntry

data class RoomLeaderboardUiState(
    val entries: List<RoomLeaderboardEntry> = emptyList(),
    val currentUserEntry: RoomLeaderboardEntry? = null,
    val isLoading: Boolean = true,
    val totalAssignmentsPosted: Int = 0,
    val totalCompetitors: Int = 0
)
