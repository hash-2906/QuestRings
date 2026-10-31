package com.questrings.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.components.QuestBottomBar
import com.questrings.core.designsystem.components.QuestChip
import com.questrings.core.designsystem.components.QuestNavigationTab
import com.questrings.core.designsystem.components.QuestTopBar
import com.questrings.core.designsystem.components.StreakBadge
import com.questrings.core.designsystem.components.XpProgressBar
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.presentation.home.components.AddHabitSheet
import com.questrings.presentation.home.components.CategoryTabRow
import com.questrings.presentation.home.components.HabitCard
import com.questrings.presentation.home.components.HeroRingsCard
import com.questrings.presentation.home.components.LevelUpDialog
import com.questrings.presentation.home.components.RingConfetti
import com.questrings.presentation.home.components.WeeklyMomentumStrip
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToShowcase: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToRooms: () -> Unit = {},
    onNavigateToSocial: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptics = com.questrings.core.haptics.rememberQuestHaptics()

    LaunchedEffect(Unit) {
        viewModel.navigationEffect.collect { effect ->
            when (effect) {
                is HomeNavigationEffect.NavigateToAuth -> onNavigateToAuth()
            }
        }
    }

    // Auto dismiss toast after 2.5s
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(2500)
            viewModel.onEvent(HomeUiEvent.DismissToast)
        }
    }

    LaunchedEffect(uiState.levelUpEvent) {
        if (uiState.levelUpEvent != null) {
            haptics.heavySuccess()
        }
    }

    LaunchedEffect(uiState.showConfetti) {
        if (uiState.showConfetti) {
            haptics.heavySuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuestTheme.colors.backgroundPrimary,
        topBar = {
            QuestTopBar(
                level = uiState.stats.level,
                titleTier = uiState.user?.displayName ?: "ShadowKnight",
                streakDays = uiState.stats.currentStreak,
                onResetClick = { /* auto-managed at midnight */ }
            )
        },
        bottomBar = {
            QuestBottomBar(
                currentTab = QuestNavigationTab.Rings,
                onTabSelected = { tab ->
                    when (tab) {
                        QuestNavigationTab.Tasks -> onNavigateToTasks()
                        QuestNavigationTab.Rooms -> onNavigateToRooms()
                        QuestNavigationTab.Social -> onNavigateToSocial()
                        QuestNavigationTab.Profile -> onNavigateToStats()
                        QuestNavigationTab.Rings -> {}
                    }
                },
                taskBadgeCount = uiState.habits.count { !it.isDoneToday }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(HomeUiEvent.OpenAddHabitSheet()) },
                containerColor = QuestTheme.colors.levelPurple,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Quest",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // User Banner & XP Progress
                item {
                    val avatarColor = try {
                        Color(android.graphics.Color.parseColor(uiState.user?.avatarColorHex ?: "#7E5DFF"))
                    } catch (_: Exception) {
                        QuestTheme.colors.levelPurple
                    }

                    val initials = (uiState.user?.displayName ?: "SK")
                        .trim()
                        .split("\\s+".toRegex())
                        .filter { it.isNotEmpty() }
                        .let { parts ->
                            if (parts.size >= 2) "${parts[0].first()}${parts[1].first()}".uppercase()
                            else parts.firstOrNull()?.take(2)?.uppercase() ?: "QR"
                        }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(avatarColor)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    style = QuestTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                )
                            }

                            Column {
                                Text(
                                    text = uiState.user?.displayName ?: "ShadowKnight",
                                    style = QuestTheme.typography.headingMedium.copy(fontSize = 17.sp),
                                    color = QuestTheme.colors.textPrimary
                                )
                                Text(
                                    text = "Rank ${uiState.stats.level} • ${uiState.stats.xp} Total XP",
                                    style = QuestTheme.typography.monoLabel.copy(fontSize = 11.sp),
                                    color = QuestTheme.colors.textSecondary
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            QuestChip(
                                text = "Stats",
                                icon = Icons.Default.Leaderboard,
                                textColor = QuestTheme.colors.disciplineBlue,
                                backgroundColor = QuestTheme.colors.disciplineBlue.copy(alpha = 0.12f),
                                borderColor = QuestTheme.colors.disciplineBlue.copy(alpha = 0.35f),
                                onClick = onNavigateToStats
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    XpProgressBar(
                        currentXp = uiState.stats.xp % 500,
                        targetXp = 500
                    )
                }

                // 7-day Weekly Momentum Strip
                item {
                    WeeklyMomentumStrip(
                        momentumList = uiState.weeklyMomentum,
                        onDayClick = { /* Could filter by past date */ }
                    )
                }

                // Concentric Hero Rings Card
                item {
                    val vCount = uiState.getCategoryCount(com.questrings.domain.model.HabitCategory.VITALITY)
                    val iCount = uiState.getCategoryCount(com.questrings.domain.model.HabitCategory.INTELLECT)
                    val dCount = uiState.getCategoryCount(com.questrings.domain.model.HabitCategory.DISCIPLINE)

                    HeroRingsCard(
                        ringData = uiState.ringData,
                        vitalityCompleted = vCount.first,
                        vitalityTotal = vCount.second,
                        intellectCompleted = iCount.first,
                        intellectTotal = iCount.second,
                        disciplineCompleted = dCount.first,
                        disciplineTotal = dCount.second
                    )
                }

                // Category Filter Tabs
                item {
                    val vCount = uiState.getCategoryCount(com.questrings.domain.model.HabitCategory.VITALITY)
                    val iCount = uiState.getCategoryCount(com.questrings.domain.model.HabitCategory.INTELLECT)
                    val dCount = uiState.getCategoryCount(com.questrings.domain.model.HabitCategory.DISCIPLINE)

                    CategoryTabRow(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { cat -> viewModel.onEvent(HomeUiEvent.SelectCategory(cat)) },
                        vitalityCount = vCount,
                        intellectCount = iCount,
                        disciplineCount = dCount
                    )
                }

                // Habit Cards List
                if (uiState.isLoading) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                com.questrings.core.designsystem.components.skeletons.HabitCardSkeleton()
                            }
                        }
                    }
                } else if (uiState.filteredHabits.isEmpty()) {
                    item {
                        com.questrings.core.designsystem.components.QuestEmptyState(
                            iconEmoji = "⚡",
                            title = "No Active Quests",
                            description = "No daily habits forged in this category yet. Take on a new challenge and fuel your rings!",
                            actionLabel = "+ Forge Habit Quest",
                            onActionClick = {
                                haptics.lightImpact()
                                viewModel.onEvent(HomeUiEvent.OpenAddHabitSheet())
                            }
                        )
                    }
                } else {
                    items(
                        items = uiState.filteredHabits,
                        key = { it.habit.id }
                    ) { habitWithStatus ->
                        HabitCard(
                            habit = habitWithStatus.habit,
                            isDone = habitWithStatus.isDoneToday,
                            onToggle = {
                                haptics.lightImpact()
                                viewModel.onEvent(HomeUiEvent.ToggleHabit(habitWithStatus.habit.id))
                            },
                            onEdit = {
                                haptics.lightImpact()
                                viewModel.onEvent(HomeUiEvent.OpenAddHabitSheet(habitWithStatus.habit))
                            },
                            onDelete = {
                                haptics.lightImpact()
                                viewModel.onEvent(HomeUiEvent.DeleteHabit(habitWithStatus.habit.id))
                            }
                        )
                    }
                }
            }

            // Confetti Overlay
            RingConfetti(
                visible = uiState.showConfetti,
                onAnimationEnd = { viewModel.onEvent(HomeUiEvent.DismissConfetti) }
            )

            // Level Up Dialog
            if (uiState.levelUpEvent != null) {
                LevelUpDialog(
                    newLevel = uiState.levelUpEvent!!,
                    onDismiss = { viewModel.onEvent(HomeUiEvent.DismissLevelUp) }
                )
            }

            // Toast feedback popup
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                uiState.toastMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(QuestTheme.colors.backgroundCard)
                            .border(1.dp, QuestTheme.colors.streakOrange.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = msg,
                            style = QuestTheme.typography.bodyMedium.copy(
                                color = QuestTheme.colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Add/Edit
    if (uiState.isAddHabitSheetOpen) {
        AddHabitSheet(
            sheetState = sheetState,
            initialHabit = uiState.editingHabit,
            onDismiss = { viewModel.onEvent(HomeUiEvent.CloseAddHabitSheet) },
            onSave = { title, goal, category, icon ->
                viewModel.onEvent(HomeUiEvent.SaveHabit(title, goal, category, icon))
            }
        )
    }
}
