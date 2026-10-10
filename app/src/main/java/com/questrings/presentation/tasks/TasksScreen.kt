package com.questrings.presentation.tasks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.questrings.core.designsystem.components.QuestBottomBar
import com.questrings.core.designsystem.components.QuestNavigationTab
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.presentation.tasks.components.AddTaskSheet
import com.questrings.presentation.tasks.components.TaskStatusFilterChips
import com.questrings.presentation.tasks.views.TaskCalendarView
import com.questrings.presentation.tasks.views.TaskListView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onNavigateToRings: () -> Unit,
    onNavigateToRooms: () -> Unit = {},
    onNavigateToSocial: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val addTaskSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    // Toast Auto-Dismiss
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(2500)
            viewModel.onEvent(TasksUiEvent.DismissToast)
        }
    }

    // Snackbar Undo Handler
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onEvent(TasksUiEvent.UndoDeleteTask)
            }
            viewModel.onEvent(TasksUiEvent.DismissSnackbar)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = QuestTheme.colors.backgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SCHEDULED QUESTS",
                            style = QuestTheme.typography.heroDisplay.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestTheme.colors.textPrimary
                            )
                        )
                        Text(
                            text = "Make time count.",
                            style = QuestTheme.typography.bodySmall.copy(
                                color = QuestTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val nextMode = if (uiState.viewMode == TaskViewMode.LIST) TaskViewMode.CALENDAR else TaskViewMode.LIST
                            viewModel.onEvent(TasksUiEvent.SetViewMode(nextMode))
                        }
                    ) {
                        Icon(
                            imageVector = if (uiState.viewMode == TaskViewMode.LIST) Icons.Default.CalendarMonth else Icons.Default.ViewList,
                            contentDescription = "Toggle View",
                            tint = QuestTheme.colors.disciplineBlue
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
                    when (tab) {
                        QuestNavigationTab.Rings -> onNavigateToRings()
                        QuestNavigationTab.Rooms -> onNavigateToRooms()
                        QuestNavigationTab.Social -> onNavigateToSocial()
                        QuestNavigationTab.Profile -> onNavigateToStats()
                        QuestNavigationTab.Tasks -> {}
                    }
                },
                taskBadgeCount = uiState.tasks.count { it.liveStatus == com.questrings.domain.model.TaskStatus.ACTIVE }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TasksUiEvent.OpenAddTaskSheet) },
                containerColor = QuestTheme.colors.levelPurple,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Filter chips in List mode
                if (uiState.viewMode == TaskViewMode.LIST) {
                    TaskStatusFilterChips(
                        selectedFilter = uiState.filterStatus,
                        onFilterSelected = { viewModel.onEvent(TasksUiEvent.SetFilter(it)) },
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                    )
                }

                // AnimatedContent toggle between List and Calendar
                AnimatedContent(
                    targetState = uiState.viewMode,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.96f)) togetherWith fadeOut()
                    },
                    label = "viewModeTransition",
                    modifier = Modifier.weight(1f)
                ) { mode ->
                    when (mode) {
                        TaskViewMode.LIST -> {
                            TaskListView(
                                tasks = uiState.filteredTasks,
                                onCompleteTask = { viewModel.onEvent(TasksUiEvent.CompleteTask(it)) },
                                onDeleteTask = { viewModel.onEvent(TasksUiEvent.DeleteTask(it)) }
                            )
                        }
                        TaskViewMode.CALENDAR -> {
                            TaskCalendarView(
                                tasks = uiState.tasks,
                                selectedDate = uiState.selectedDate,
                                onDateSelected = { viewModel.onEvent(TasksUiEvent.SelectDate(it)) },
                                onCompleteTask = { viewModel.onEvent(TasksUiEvent.CompleteTask(it)) },
                                onDeleteTask = { viewModel.onEvent(TasksUiEvent.DeleteTask(it)) }
                            )
                        }
                    }
                }
            }

            // Bottom XP Toast
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

    // Add Task Sheet
    if (uiState.isAddTaskSheetOpen) {
        AddTaskSheet(
            sheetState = addTaskSheetState,
            onDismiss = { viewModel.onEvent(TasksUiEvent.CloseAddTaskSheet) },
            onSave = { title, notes, priority, dueAt, reminders ->
                viewModel.onEvent(
                    TasksUiEvent.CreateTask(
                        title = title,
                        notes = notes,
                        priority = priority,
                        dueAt = dueAt,
                        reminders = reminders
                    )
                )
            }
        )
    }
}
