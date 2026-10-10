package com.questrings.presentation.home.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    sheetState: SheetState,
    initialHabit: Habit? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, goal: String, category: HabitCategory, icon: HabitIcon) -> Unit
) {
    var title by remember(initialHabit) { mutableStateOf(initialHabit?.title ?: "") }
    var goal by remember(initialHabit) { mutableStateOf(initialHabit?.goal ?: "") }
    var selectedCategory by remember(initialHabit) {
        mutableStateOf(initialHabit?.category ?: HabitCategory.VITALITY)
    }
    var selectedIcon by remember(initialHabit) {
        mutableStateOf(initialHabit?.icon ?: HabitIcon.TARGET)
    }

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
                text = if (initialHabit == null) "Forge New Quest" else "Reforge Quest",
                style = QuestTheme.typography.heroDisplay.copy(
                    fontSize = 20.sp,
                    color = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Habit Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Quest Title (e.g., Morning Run)") },
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

            // Goal Description
            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text("Daily Target (e.g., Run 5km)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuestTheme.colors.disciplineBlue,
                    unfocusedBorderColor = QuestTheme.colors.borderSubtle,
                    focusedTextColor = QuestTheme.colors.textPrimary,
                    unfocusedTextColor = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ring Category Selector
            Text(
                text = "AFFILIATED RING",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HabitCategory.values().forEach { category ->
                    val isSelected = category == selectedCategory
                    val ringColor = when (category) {
                        HabitCategory.VITALITY -> QuestTheme.colors.vitalityRed
                        HabitCategory.INTELLECT -> QuestTheme.colors.intellectGreen
                        HabitCategory.DISCIPLINE -> QuestTheme.colors.disciplineBlue
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) ringColor.copy(alpha = 0.2f)
                                else QuestTheme.colors.backgroundSecondary
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ringColor else QuestTheme.colors.borderSubtle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.displayName,
                            style = QuestTheme.typography.labelMedium.copy(
                                color = if (isSelected) ringColor else QuestTheme.colors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Icon Selector
            Text(
                text = "BADGE ICON",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(HabitIcon.values()) { icon ->
                    val isSelected = icon == selectedIcon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) QuestTheme.colors.levelPurple.copy(alpha = 0.3f)
                                else QuestTheme.colors.backgroundSecondary
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) QuestTheme.colors.levelPurple else QuestTheme.colors.borderSubtle,
                                shape = CircleShape
                            )
                            .clickable { selectedIcon = icon },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon.name.take(2),
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                color = if (isSelected) QuestTheme.colors.levelPurple else QuestTheme.colors.textSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            QuestButton(
                text = if (initialHabit == null) "Create Quest" else "Save Changes",
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), goal.trim(), selectedCategory, selectedIcon)
                    }
                },
                enabled = title.isNotBlank(),
                variant = QuestButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
