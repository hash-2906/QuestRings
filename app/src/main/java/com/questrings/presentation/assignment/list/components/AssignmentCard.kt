package com.questrings.presentation.assignment.list.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentOrange
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.model.assignment.ChecklistItem
import com.questrings.domain.model.assignment.CompletionVerificationStatus
import com.questrings.domain.usecase.assignments.AssignmentWithStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AssignmentCard(
    assignmentWithStatus: AssignmentWithStatus,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean = false,
    modifier: Modifier = Modifier
) {
    val assignment = assignmentWithStatus.assignment
    val liveStatus = assignmentWithStatus.liveStatus
    val userCompletion = assignmentWithStatus.currentUserCompletion

    val isChecklist = assignment.type == AssignmentType.CHECKLIST

    // Pulsing alpha for active/grace
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statusPulse"
    )

    val (statusLabel, statusColor, isPulsing) = when (liveStatus) {
        AssignmentStatus.UPCOMING -> Triple("UPCOMING", TextMuted, false)
        AssignmentStatus.ACTIVE -> Triple("ACTIVE", AccentOrange, true)
        AssignmentStatus.GRACE -> Triple("GRACE PERIOD", Color(0xFFFF2D55), true)
        AssignmentStatus.CLOSED -> Triple("CLOSED", TextMuted, false)
    }

    val typeColor = if (isChecklist) AccentPurple else DisciplineBlue
    val typeLabel = if (isChecklist) "CHECKLIST" else "TASK"

    // Time window progress
    val now = System.currentTimeMillis()
    val totalWindow = (assignment.dueAt - assignment.createdAt).coerceAtLeast(1L)
    val elapsed = (now - assignment.createdAt).coerceIn(0L, totalWindow)
    val progress = (elapsed.toFloat() / totalWindow.toFloat()).coerceIn(0f, 1f)

    val dateFormat = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(assignment.dueAt))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Type Badge + Title + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Type Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(typeColor.copy(alpha = 0.15f))
                            .border(1.dp, typeColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = typeLabel,
                            color = typeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = assignment.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Pill
                    Box(
                        modifier = Modifier
                            .alpha(if (isPulsing) pulseAlpha else 1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (canDelete) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Assignment",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (assignment.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = assignment.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Subtitle: Items + Completed count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isChecklist) {
                    val checkedCount = userCompletion?.checkedItemIds?.size ?: 0
                    val totalItems = assignment.checklistItems.size
                    Text(
                        text = "📋 $checkedCount/$totalItems items · 👥 ${assignment.totalCompletions} completed",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = "👥 ${assignment.totalCompletions} adventurers finished",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "⏰ Due: $dueDateStr",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Elapsed Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (liveStatus == AssignmentStatus.GRACE) Color(0xFFFF2D55) else typeColor,
                trackColor = BorderSubtle
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User completion status
                when {
                    userCompletion == null -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(typeColor.copy(alpha = 0.1f))
                                .border(1.dp, typeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable(onClick = onClick)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "COMPLETE QUEST →",
                                color = typeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    userCompletion.verificationStatus == CompletionVerificationStatus.PENDING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Awaiting verification",
                                color = AccentOrange,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    userCompletion.verificationStatus == CompletionVerificationStatus.APPROVED ||
                    userCompletion.verificationStatus == CompletionVerificationStatus.AUTO_APPROVED -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Rank #${userCompletion.rank ?: 1} · ${userCompletion.pointsAwarded ?: 100} pts",
                                color = AccentGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    userCompletion.verificationStatus == CompletionVerificationStatus.REJECTED -> {
                        Text(
                            text = "✗ Not Approved",
                            color = Color(0xFFFF2D55),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Leaderboard →",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = onClick)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AssignmentCardPreview() {
    QuestRingsTheme {
        AssignmentCard(
            assignmentWithStatus = AssignmentWithStatus(
                assignment = Assignment(
                    id = "asg-1",
                    roomId = "room-1",
                    createdBy = "user-1",
                    title = "Solve LeetCode Biweekly Contest",
                    description = "Submit minimum 3 problems and verify algorithmic efficiency on Discord.",
                    type = AssignmentType.CHECKLIST,
                    checklistItems = listOf(
                        ChecklistItem("1", "Read instructions", 0),
                        ChecklistItem("2", "Submit solutions", 1),
                        ChecklistItem("3", "Write reflection", 2)
                    ),
                    status = AssignmentStatus.ACTIVE,
                    requiresVerification = false,
                    createdAt = System.currentTimeMillis() - 3600000L,
                    dueAt = System.currentTimeMillis() + 86400000L,
                    totalCompletions = 14,
                    maxPoints = 100
                ),
                liveStatus = AssignmentStatus.ACTIVE,
                currentUserCompletion = null,
                completionCount = 14
            ),
            onClick = {},
            onDelete = {},
            canDelete = true
        )
    }
}
