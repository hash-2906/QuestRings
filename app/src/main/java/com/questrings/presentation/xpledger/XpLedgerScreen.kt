package com.questrings.presentation.xpledger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSource
import com.questrings.domain.model.xp.XpSummary
import com.questrings.presentation.medal.components.MedalBadge
import com.questrings.presentation.xpledger.components.XpEventDetailSheet
import com.questrings.presentation.xpledger.components.XpEventRow
import com.questrings.presentation.xpledger.components.XpSourceFilterChips
import com.questrings.presentation.xpledger.components.XpWeeklySummaryCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun XpLedgerScreen(
    onNavigateBack: () -> Unit,
    viewModel: XpLedgerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    XpLedgerContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onFilterSelected = viewModel::setFilter,
        onEventClick = viewModel::selectEvent,
        onDismissDetail = { viewModel.selectEvent(null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XpLedgerContent(
    uiState: XpLedgerUiState,
    onNavigateBack: () -> Unit,
    onFilterSelected: (com.questrings.domain.repository.XpSourceFilter) -> Unit,
    onEventClick: (XpEvent) -> Unit,
    onDismissDetail: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "XP LEDGER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = TextPrimary
                        )
                        uiState.summary?.let { sum ->
                            Text(
                                text = "${"%,d".format(sum.totalXp)} Total XP • Level ${sum.level}",
                                style = MaterialTheme.typography.labelSmall,
                                color = DisciplineBlue
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    uiState.summary?.let { sum ->
                        MedalBadge(
                            tier = sum.currentMedal,
                            size = 28.dp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary
                )
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RoyalPurple)
            }
        } else {
            val groupedEvents = groupEventsByDate(uiState.events)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Weekly chart card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    XpWeeklySummaryCard(
                        weeklyXpTotal = uiState.summary?.xpThisWeek ?: 0,
                        weeklyStats = uiState.weeklyStats
                    )
                }

                // Filter chips
                item {
                    XpSourceFilterChips(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = onFilterSelected
                    )
                }

                if (uiState.events.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "📜",
                                    fontSize = 40.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No XP events found",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Complete quests, habits, or assignments to earn XP",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    groupedEvents.forEach { (dateGroup, eventsInGroup) ->
                        item {
                            Text(
                                text = dateGroup.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = TextMuted,
                                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                            )
                        }

                        items(eventsInGroup, key = { it.id }) { event ->
                            XpEventRow(
                                event = event,
                                onClick = { onEventClick(event) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Event detail bottom sheet
        uiState.selectedEvent?.let { event ->
            XpEventDetailSheet(
                event = event,
                onDismiss = onDismissDetail
            )
        }
    }
}

private fun groupEventsByDate(events: List<XpEvent>): Map<String, List<XpEvent>> {
    val cal = Calendar.getInstance()
    val todayKey = getDayKey(cal)
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayKey = getDayKey(cal)

    return events.groupBy { event ->
        val eventCal = Calendar.getInstance().apply { timeInMillis = event.earnedAt }
        val eventKey = getDayKey(eventCal)
        when (eventKey) {
            todayKey -> "Today"
            yesterdayKey -> "Yesterday"
            else -> SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date(event.earnedAt))
        }
    }
}

private fun getDayKey(cal: Calendar): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

@Preview(showBackground = true)
@Composable
fun XpLedgerScreenPreview() {
    QuestRingsTheme {
        XpLedgerContent(
            uiState = XpLedgerUiState(
                isLoading = false,
                summary = XpSummary(
                    totalXp = 4850,
                    level = 14,
                    currentMedal = MedalTier.SILVER,
                    nextMedal = null,
                    progressToNextMedal = 0.65f,
                    xpThisWeek = 1420,
                    xpBySource = emptyMap()
                ),
                events = listOf(
                    XpEvent(
                        id = "1",
                        userId = "u1",
                        source = XpSource.TASK_COMPLETION,
                        baseXp = 100,
                        bonusXp = 25,
                        totalXp = 125,
                        multiplier = 1.25f,
                        description = "Complete Clean Architecture refactor",
                        sourceEntityId = "t1",
                        sourceEntityTitle = "Refactor",
                        earnedAt = System.currentTimeMillis() - 10000,
                        cumulativeXpAfter = 4850
                    )
                )
            ),
            onNavigateBack = {},
            onFilterSelected = {},
            onEventClick = {},
            onDismissDetail = {}
        )
    }
}
