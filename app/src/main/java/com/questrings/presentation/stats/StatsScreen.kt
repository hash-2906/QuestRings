package com.questrings.presentation.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.components.QuestBottomBar
import com.questrings.core.designsystem.components.QuestNavigationTab
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.presentation.stats.components.LongestStreakBadge
import com.questrings.presentation.stats.components.MonthlyHeatmap
import com.questrings.presentation.stats.components.StatSummaryGrid
import com.questrings.presentation.stats.components.XpByCategoryChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuestTheme.colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MASTERY & STATS",
                        style = QuestTheme.typography.heroDisplay.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestTheme.colors.textPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = QuestTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QuestTheme.colors.backgroundPrimary
                )
            )
        },
        bottomBar = {
            QuestBottomBar(
                currentTab = QuestNavigationTab.Tasks,
                onTabSelected = { tab ->
                    if (tab == QuestNavigationTab.Rings) {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Summary 2x2 Grid
            item {
                StatSummaryGrid(
                    currentStreak = uiState.stats.currentStreak,
                    longestStreak = uiState.stats.longestStreak,
                    totalCompleted = uiState.stats.totalHabitsCompleted,
                    level = uiState.stats.level
                )
            }

            // Longest streak banner
            item {
                LongestStreakBadge(
                    longestStreak = uiState.stats.longestStreak,
                    currentStreak = uiState.stats.currentStreak
                )
            }

            // Monthly Heatmap
            item {
                MonthlyHeatmap(history = uiState.heatmapHistory)
            }

            // XP Breakdown Chart
            item {
                XpByCategoryChart(xpByCategory = uiState.stats.xpByCategory)
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
