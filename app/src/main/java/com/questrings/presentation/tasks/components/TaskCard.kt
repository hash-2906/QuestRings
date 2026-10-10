package com.questrings.presentation.tasks.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskCountdown
import com.questrings.domain.model.TaskPriority
import com.questrings.domain.model.TaskStatus
import com.questrings.presentation.tasks.TaskWithLiveStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskCard(
    taskWithStatus: TaskWithLiveStatus,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskWithStatus.task
    val liveStatus = taskWithStatus.liveStatus
    val countdown = taskWithStatus.countdown

    val priorityColor = when (task.priority) {
        TaskPriority.LOW -> QuestTheme.colors.intellectGreen
        TaskPriority.MEDIUM -> QuestTheme.colors.disciplineBlue
        TaskPriority.HIGH -> QuestTheme.colors.streakOrange
        TaskPriority.URGENT -> QuestTheme.colors.vitalityRed
    }

    val isCompleted = liveStatus == TaskStatus.COMPLETED
    val isFailed = liveStatus == TaskStatus.FAILED
    val isGrace = liveStatus == TaskStatus.GRACE_PERIOD

    val infiniteTransition = rememberInfiniteTransition(label = "gracePulse")
    val graceAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "graceAlpha"
    )

    val cardAlpha = if (isCompleted) 0.65f else 1.0f

    val progressBarColor = when {
        isCompleted -> QuestTheme.colors.intellectGreen
        isFailed -> Color(0xFF44444D)
        isGrace -> QuestTheme.colors.vitalityRed.copy(alpha = graceAlpha)
        else -> priorityColor
    }

    val formattedDue = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(task.dueAt))

    QuestCard(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Priority Badge + Title + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(priorityColor.copy(alpha = 0.15f))
                        .border(1.dp, priorityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = task.priority.badgeLabel,
                        style = QuestTheme.typography.monoLabel.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Title
                Text(
                    text = task.title,
                    style = QuestTheme.typography.headingMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) QuestTheme.colors.textMuted else QuestTheme.colors.textPrimary,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Quest",
                        tint = QuestTheme.colors.textMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Notes (if any, max 2 lines)
            if (task.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.notes,
                    style = QuestTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = QuestTheme.colors.textSecondary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Due Date & Reminders info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = QuestTheme.colors.textMuted,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Due: $formattedDue",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.textSecondary
                    )
                )

                if (task.reminders.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${task.reminders.size} reminder${if (task.reminders.size > 1) "s" else ""}",
                        style = QuestTheme.typography.monoLabel.copy(
                            fontSize = 10.sp,
                            color = QuestTheme.colors.levelPurple
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF202025))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (isCompleted || isFailed) 1.0f else countdown.progressFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(progressBarColor)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Countdown label & Mark Complete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskCountdownDisplay(
                    countdown = countdown,
                    color = when {
                        isCompleted -> QuestTheme.colors.intellectGreen
                        isFailed -> Color(0xFFFF2D55).copy(alpha = 0.7f)
                        isGrace -> QuestTheme.colors.streakOrange
                        else -> priorityColor
                    }
                )

                if (!isCompleted && !isFailed) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(priorityColor.copy(alpha = 0.12f))
                            .border(1.dp, priorityColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { onComplete() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isGrace) "Complete (Grace XP)" else "Mark Complete",
                            style = QuestTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        )
                    }
                } else if (isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = QuestTheme.colors.intellectGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+${task.xpAwarded} XP",
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestTheme.colors.intellectGreen
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TaskCardPreview() {
    QuestRingsTheme {
        val sampleTask = Task(
            id = "t1",
            title = "Defeat the Quarterly Roadmap",
            notes = "Consolidate Q4 objectives and align engineering deliverables with client feedback.",
            priority = TaskPriority.URGENT,
            status = TaskStatus.ACTIVE,
            createdAt = System.currentTimeMillis() - 3600000,
            dueAt = System.currentTimeMillis() + 7200000
        )
        TaskCard(
            taskWithStatus = TaskWithLiveStatus(
                task = sampleTask,
                liveStatus = TaskStatus.ACTIVE,
                countdown = TaskCountdown(
                    totalMs = 10800000,
                    remainingMs = 7200000,
                    progressFraction = 0.66f,
                    displayLabel = "02:00:00",
                    isCountingDown = true,
                    isInGrace = false
                )
            ),
            onComplete = {},
            onDelete = {}
        )
    }
}
