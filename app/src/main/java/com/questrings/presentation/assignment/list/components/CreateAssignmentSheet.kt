package com.questrings.presentation.assignment.list.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.components.QuestTextField
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentOrange
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.AssignmentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAssignmentSheet(
    sheetState: SheetState,
    requiresVerification: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        description: String,
        type: AssignmentType,
        items: List<String>,
        dueAt: Long
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AssignmentType.SIMPLE) }
    val checklistItems = remember { mutableStateListOf("Read task specifications", "Submit work notes") }
    var newItemText by remember { mutableStateOf("") }

    // Default to +24 hours
    var dueAt by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000L) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundPrimary,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "POST A QUEST",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Set a competitive challenge for your chamber.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Segmented Type Toggle
            Text(
                text = "Quest Type",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isSimple = selectedType == AssignmentType.SIMPLE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSimple) DisciplineBlue.copy(alpha = 0.2f) else BackgroundCard)
                        .border(
                            width = 1.5.dp,
                            color = if (isSimple) DisciplineBlue else BorderCard,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedType = AssignmentType.SIMPLE }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = if (isSimple) DisciplineBlue else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SIMPLE TASK",
                            color = if (isSimple) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                val isChecklist = selectedType == AssignmentType.CHECKLIST
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isChecklist) AccentPurple.copy(alpha = 0.2f) else BackgroundCard)
                        .border(
                            width = 1.5.dp,
                            color = if (isChecklist) AccentPurple else BorderCard,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedType = AssignmentType.CHECKLIST }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = if (isChecklist) AccentPurple else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CHECKLIST",
                            color = if (isChecklist) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quest Title
            Text(
                text = "Quest Title *",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            QuestTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "e.g., Deliver Quarterly Architecture RFC"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quest Description
            Text(
                text = "Description (Optional)",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            QuestTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Provide guidelines, milestones, and deliverables..."
            )

            // If Checklist, display items editor
            if (selectedType == AssignmentType.CHECKLIST) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Checklist Steps (Min 2, Max 15)",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                checklistItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${index + 1}.",
                            color = AccentPurple,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BackgroundCard)
                                .border(1.dp, BorderCard, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(text = item, color = TextPrimary, fontSize = 13.sp)
                        }

                        if (checklistItems.size > 2) {
                            IconButton(
                                onClick = { checklistItems.removeAt(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Item",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                if (checklistItems.size < 15) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuestTextField(
                            value = newItemText,
                            onValueChange = { newItemText = it },
                            placeholder = "Add next step...",
                            modifier = Modifier.weight(1f)
                        )
                        QuestButton(
                            text = "Add",
                            icon = Icons.Default.Add,
                            variant = QuestButtonVariant.Secondary,
                            enabled = newItemText.isNotBlank(),
                            onClick = {
                                checklistItems.add(newItemText.trim())
                                newItemText = ""
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Due Date & Time Presets
            Text(
                text = "Quest Deadline",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val dateFormat = SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
            Text(
                text = "Due: ${dateFormat.format(Date(dueAt))}",
                color = AccentOrange,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("+6h", 6 * 3600000L),
                    Pair("+12h", 12 * 3600000L),
                    Pair("+24h", 24 * 3600000L),
                    Pair("+48h", 48 * 3600000L),
                    Pair("+1w", 7 * 86400000L)
                ).forEach { (label, duration) ->
                    val isSelected = dueAt == System.currentTimeMillis() + duration
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentPurple.copy(alpha = 0.2f) else BackgroundCard)
                            .border(1.dp, if (isSelected) AccentPurple else BorderCard, RoundedCornerShape(8.dp))
                            .clickable { dueAt = System.currentTimeMillis() + duration }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) AccentPurple else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Verification Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard)
                    .border(1.dp, BorderCard, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (requiresVerification) AccentOrange else AccentGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = if (requiresVerification) "Verification: Required" else "Verification: Auto-Approved",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (requiresVerification)
                                "Submissions will enter the Admin/Mod approval queue before scores appear."
                            else
                                "Submissions are instantly scored and ranked upon completion.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // XP Potential Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentPurple.copy(alpha = 0.08f))
                    .border(1.dp, AccentPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "⚡ XP ECONOMY PREVIEW",
                        color = AccentPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rank 1 earns up to 187 XP (100 base + 25 speed bonus × 1.5 multiplier). Grace period submissions yield 50% points.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            val canSubmit = title.isNotBlank() && (selectedType == AssignmentType.SIMPLE || checklistItems.size >= 2)
            QuestButton(
                text = "POST ASSIGNMENT →",
                variant = QuestButtonVariant.Primary,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSubmit(
                        title.trim(),
                        description.trim(),
                        selectedType,
                        if (selectedType == AssignmentType.CHECKLIST) checklistItems.toList() else emptyList(),
                        dueAt
                    )
                }
            )
        }
    }
}
