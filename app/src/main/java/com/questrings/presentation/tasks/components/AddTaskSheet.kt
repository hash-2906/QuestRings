package com.questrings.presentation.tasks.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.TaskPriority
import com.questrings.domain.model.TaskReminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTaskSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        notes: String,
        priority: TaskPriority,
        dueAt: Long,
        reminders: List<TaskReminder>
    ) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    // Due date state (default: tomorrow at 6:00 PM)
    val defaultCalendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    var dueCalendar by remember { mutableStateOf(defaultCalendar) }

    var reminders by remember { mutableStateOf(emptyList<TaskReminder>()) }
    var isReminderPickerOpen by remember { mutableStateOf(false) }
    val reminderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val now = System.currentTimeMillis()
    val isDueInFuture = dueCalendar.timeInMillis > now + 300_000L // at least 5 mins in future
    val isTitleValid = title.isNotBlank()

    val formattedDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(dueCalendar.time)
    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(dueCalendar.time)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuestTheme.colors.backgroundCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Schedule a Quest",
                style = QuestTheme.typography.heroDisplay.copy(
                    fontSize = 22.sp,
                    color = QuestTheme.colors.textPrimary
                )
            )
            Text(
                text = "Give your next win a deadline.",
                style = QuestTheme.typography.bodySmall.copy(
                    color = QuestTheme.colors.textSecondary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Quest Title *") },
                placeholder = { Text("e.g., Deliver Quarterly Strategy") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuestTheme.colors.disciplineBlue,
                    unfocusedBorderColor = QuestTheme.colors.borderSubtle,
                    focusedTextColor = QuestTheme.colors.textPrimary,
                    unfocusedTextColor = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Quest Notes / Description (optional)") },
                placeholder = { Text("Key milestones, deliverables or objectives") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuestTheme.colors.disciplineBlue,
                    unfocusedBorderColor = QuestTheme.colors.borderSubtle,
                    focusedTextColor = QuestTheme.colors.textPrimary,
                    unfocusedTextColor = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Priority Selector
            Text(
                text = "PRIORITY & XP STAKES",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrioritySelector(
                selectedPriority = priority,
                onPrioritySelected = { priority = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Due Date & Time Pickers
            Text(
                text = "DEADLINE",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(QuestTheme.colors.backgroundSecondary)
                        .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = dueCalendar.timeInMillis
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    }
                                    dueCalendar = newCal
                                },
                                dueCalendar.get(Calendar.YEAR),
                                dueCalendar.get(Calendar.MONTH),
                                dueCalendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = QuestTheme.colors.disciplineBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formattedDate,
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                color = QuestTheme.colors.textPrimary
                            )
                        )
                    }
                }

                // Time Button
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(QuestTheme.colors.backgroundSecondary)
                        .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = dueCalendar.timeInMillis
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                    }
                                    dueCalendar = newCal
                                },
                                dueCalendar.get(Calendar.HOUR_OF_DAY),
                                dueCalendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = QuestTheme.colors.disciplineBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formattedTime,
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                color = QuestTheme.colors.textPrimary
                            )
                        )
                    }
                }
            }

            if (!isDueInFuture) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Due time must be at least 5 minutes in the future",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 10.sp,
                        color = QuestTheme.colors.vitalityRed
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Reminders Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REMINDERS",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )
                Text(
                    text = "+ Add Reminder",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.levelPurple,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { isReminderPickerOpen = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(QuestTheme.colors.backgroundSecondary)
                        .clickable { isReminderPickerOpen = true }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = QuestTheme.colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "No reminders scheduled. Tap to configure alerts",
                            style = QuestTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = QuestTheme.colors.textMuted
                            )
                        )
                    }
                }
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    reminders.forEach { reminder ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(QuestTheme.colors.backgroundSecondary)
                                .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "⏰ ${reminder.label}",
                                style = QuestTheme.typography.monoLabel.copy(
                                    fontSize = 11.sp,
                                    color = QuestTheme.colors.textPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = QuestTheme.colors.textMuted,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { reminders = reminders.filterNot { it.id == reminder.id } }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // XP Preview
            val priorityColor = when (priority) {
                TaskPriority.LOW -> QuestTheme.colors.intellectGreen
                TaskPriority.MEDIUM -> QuestTheme.colors.disciplineBlue
                TaskPriority.HIGH -> QuestTheme.colors.streakOrange
                TaskPriority.URGENT -> QuestTheme.colors.vitalityRed
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(priorityColor.copy(alpha = 0.1f))
                    .border(1.dp, priorityColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡ Completing this quest earns ${priority.xpReward} XP",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = priorityColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            QuestButton(
                text = "Schedule Quest →",
                onClick = {
                    if (isTitleValid && isDueInFuture) {
                        onSave(
                            title.trim(),
                            notes.trim(),
                            priority,
                            dueCalendar.timeInMillis,
                            reminders
                        )
                    }
                },
                enabled = isTitleValid && isDueInFuture,
                variant = QuestButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Nested Reminder Picker Sheet
    if (isReminderPickerOpen) {
        ReminderPickerSheet(
            sheetState = reminderSheetState,
            currentReminders = reminders,
            onRemindersUpdated = { updated -> reminders = updated },
            onDismiss = { isReminderPickerOpen = false }
        )
    }
}
