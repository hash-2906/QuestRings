package com.questrings.presentation.tasks.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskPriority
import com.questrings.domain.model.TaskStatus
import com.questrings.presentation.tasks.TaskWithLiveStatus
import com.questrings.presentation.tasks.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListView(
    tasks: List<TaskWithLiveStatus>,
    onCompleteTask: (String) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            com.questrings.core.designsystem.components.QuestEmptyState(
                iconEmoji = "⚔️",
                title = "No Scheduled Quests",
                description = "No active deadlines in this view. Schedule side quests or boss missions to earn priority XP."
            )
        }
        return
    }

    // Grouping
    val bossQuests = tasks.filter { it.liveStatus != TaskStatus.COMPLETED && it.liveStatus != TaskStatus.FAILED && it.task.priority == TaskPriority.URGENT }
    val mainQuests = tasks.filter { it.liveStatus != TaskStatus.COMPLETED && it.liveStatus != TaskStatus.FAILED && it.task.priority == TaskPriority.HIGH }
    val sideQuests = tasks.filter { it.liveStatus != TaskStatus.COMPLETED && it.liveStatus != TaskStatus.FAILED && it.task.priority == TaskPriority.MEDIUM }
    val easyQuests = tasks.filter { it.liveStatus != TaskStatus.COMPLETED && it.liveStatus != TaskStatus.FAILED && it.task.priority == TaskPriority.LOW }
    val completedQuests = tasks.filter { it.liveStatus == TaskStatus.COMPLETED }
    val failedQuests = tasks.filter { it.liveStatus == TaskStatus.FAILED }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (bossQuests.isNotEmpty()) {
            item {
                SectionHeader(title = "BOSS QUESTS", count = bossQuests.size, color = QuestTheme.colors.vitalityRed)
            }
            items(bossQuests, key = { it.task.id }) { item ->
                DismissibleTaskItem(
                    taskItem = item,
                    onComplete = { onCompleteTask(item.task.id) },
                    onDelete = { onDeleteTask(item.task) }
                )
            }
        }

        if (mainQuests.isNotEmpty()) {
            item {
                SectionHeader(title = "MAIN QUESTS", count = mainQuests.size, color = QuestTheme.colors.streakOrange)
            }
            items(mainQuests, key = { it.task.id }) { item ->
                DismissibleTaskItem(
                    taskItem = item,
                    onComplete = { onCompleteTask(item.task.id) },
                    onDelete = { onDeleteTask(item.task) }
                )
            }
        }

        if (sideQuests.isNotEmpty()) {
            item {
                SectionHeader(title = "SIDE QUESTS", count = sideQuests.size, color = QuestTheme.colors.disciplineBlue)
            }
            items(sideQuests, key = { it.task.id }) { item ->
                DismissibleTaskItem(
                    taskItem = item,
                    onComplete = { onCompleteTask(item.task.id) },
                    onDelete = { onDeleteTask(item.task) }
                )
            }
        }

        if (easyQuests.isNotEmpty()) {
            item {
                SectionHeader(title = "EASY QUESTS", count = easyQuests.size, color = QuestTheme.colors.intellectGreen)
            }
            items(easyQuests, key = { it.task.id }) { item ->
                DismissibleTaskItem(
                    taskItem = item,
                    onComplete = { onCompleteTask(item.task.id) },
                    onDelete = { onDeleteTask(item.task) }
                )
            }
        }

        if (completedQuests.isNotEmpty()) {
            item {
                SectionHeader(title = "COMPLETED", count = completedQuests.size, color = QuestTheme.colors.intellectGreen)
            }
            items(completedQuests, key = { it.task.id }) { item ->
                DismissibleTaskItem(
                    taskItem = item,
                    onComplete = { onCompleteTask(item.task.id) },
                    onDelete = { onDeleteTask(item.task) }
                )
            }
        }

        if (failedQuests.isNotEmpty()) {
            item {
                SectionHeader(title = "FAILED QUESTS", count = failedQuests.size, color = QuestTheme.colors.textMuted)
            }
            items(failedQuests, key = { it.task.id }) { item ->
                DismissibleTaskItem(
                    taskItem = item,
                    onComplete = { onCompleteTask(item.task.id) },
                    onDelete = { onDeleteTask(item.task) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissibleTaskItem(
    taskItem: TaskWithLiveStatus,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF8B0000))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            TaskCard(
                taskWithStatus = taskItem,
                onComplete = onComplete,
                onDelete = onDelete
            )
        }
    )
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 1.sp
                )
            )
        }
        Text(
            text = "$count",
            style = QuestTheme.typography.monoLabel.copy(
                fontSize = 11.sp,
                color = QuestTheme.colors.textMuted
            )
        )
    }
}
