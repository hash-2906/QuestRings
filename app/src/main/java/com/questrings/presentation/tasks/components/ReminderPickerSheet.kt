package com.questrings.presentation.tasks.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.TaskReminder
import java.util.UUID

private data class PresetReminder(val minutes: Int, val label: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReminderPickerSheet(
    sheetState: SheetState,
    currentReminders: List<TaskReminder>,
    onRemindersUpdated: (List<TaskReminder>) -> Unit,
    onDismiss: () -> Unit
) {
    var remindersList by remember { mutableStateOf(currentReminders) }
    var customNumber by remember { mutableStateOf("") }
    var customUnit by remember { mutableStateOf("hours") } // "minutes", "hours", "days"

    val presets = listOf(
        PresetReminder(15, "15 min before"),
        PresetReminder(30, "30 min before"),
        PresetReminder(60, "1 hour before"),
        PresetReminder(180, "3 hours before"),
        PresetReminder(1440, "1 day before"),
        PresetReminder(2880, "2 days before")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuestTheme.colors.backgroundCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Set Quest Reminders",
                style = QuestTheme.typography.heroDisplay.copy(
                    fontSize = 18.sp,
                    color = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PRESET REMINDERS",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 10.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.forEach { preset ->
                    val isAlreadyAdded = remindersList.any { it.minutesBefore == preset.minutes }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isAlreadyAdded) QuestTheme.colors.levelPurple.copy(alpha = 0.2f)
                                else QuestTheme.colors.backgroundSecondary
                            )
                            .border(
                                1.dp,
                                if (isAlreadyAdded) QuestTheme.colors.levelPurple else QuestTheme.colors.borderSubtle,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (!isAlreadyAdded) {
                                    remindersList = remindersList + TaskReminder(
                                        id = UUID.randomUUID().toString(),
                                        taskId = "",
                                        minutesBefore = preset.minutes,
                                        label = preset.label
                                    )
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = preset.label,
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                color = if (isAlreadyAdded) QuestTheme.colors.levelPurple else QuestTheme.colors.textSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "CUSTOM INTERVAL",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 10.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customNumber,
                    onValueChange = { customNumber = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("Num", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuestTheme.colors.disciplineBlue,
                        unfocusedBorderColor = QuestTheme.colors.borderSubtle,
                        focusedTextColor = QuestTheme.colors.textPrimary,
                        unfocusedTextColor = QuestTheme.colors.textPrimary
                    )
                )

                // Unit Selector
                listOf("mins", "hours", "days").forEach { unit ->
                    val isSelected = customUnit == unit
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) QuestTheme.colors.disciplineBlue.copy(alpha = 0.2f)
                                else QuestTheme.colors.backgroundSecondary
                            )
                            .border(
                                1.dp,
                                if (isSelected) QuestTheme.colors.disciplineBlue else QuestTheme.colors.borderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { customUnit = unit }
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = unit,
                            style = QuestTheme.typography.labelMedium.copy(
                                fontSize = 11.sp,
                                color = if (isSelected) QuestTheme.colors.disciplineBlue else QuestTheme.colors.textSecondary
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val num = customNumber.toIntOrNull()
                        if (num != null && num > 0) {
                            val mins = when (customUnit) {
                                "mins" -> num
                                "hours" -> num * 60
                                "days" -> num * 1440
                                else -> num
                            }
                            // Min 5 mins, Max 7 days (10080 mins)
                            val clampedMins = mins.coerceIn(5, 10080)
                            val label = "$num $customUnit before"
                            if (remindersList.none { it.minutesBefore == clampedMins }) {
                                remindersList = remindersList + TaskReminder(
                                    id = UUID.randomUUID().toString(),
                                    taskId = "",
                                    minutesBefore = clampedMins,
                                    label = label
                                )
                                customNumber = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(QuestTheme.colors.levelPurple)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom reminder",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Configured Chips
            if (remindersList.isNotEmpty()) {
                Text(
                    text = "ACTIVE NOTIFICATIONS",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 10.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    remindersList.forEach { reminder ->
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
                                    .clickable {
                                        remindersList = remindersList.filterNot { it.id == reminder.id }
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            QuestButton(
                text = "Apply Reminders (${remindersList.size})",
                onClick = {
                    onRemindersUpdated(remindersList)
                    onDismiss()
                },
                variant = QuestButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
