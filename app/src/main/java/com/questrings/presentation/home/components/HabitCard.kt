package com.questrings.presentation.home.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitIcon

@Composable
fun HabitCard(
    habit: Habit,
    isDone: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val ringColor = when (habit.category) {
        HabitCategory.VITALITY -> QuestTheme.colors.vitalityRed
        HabitCategory.INTELLECT -> QuestTheme.colors.intellectGreen
        HabitCategory.DISCIPLINE -> QuestTheme.colors.disciplineBlue
    }

    val checkboxBg by animateColorAsState(
        targetValue = if (isDone) ringColor else Color.Transparent,
        label = "checkboxBg"
    )

    QuestCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox trigger
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(checkboxBg)
                    .border(
                        width = 2.dp,
                        color = if (isDone) ringColor else QuestTheme.colors.borderSubtle,
                        shape = CircleShape
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Habit Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggle() }
            ) {
                Text(
                    text = habit.title,
                    style = QuestTheme.typography.headingMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDone) QuestTheme.colors.textMuted else QuestTheme.colors.textPrimary,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.goal,
                        style = QuestTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = QuestTheme.colors.textSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ringColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = habit.category.displayName,
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 9.sp,
                                color = ringColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Options menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = QuestTheme.colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(QuestTheme.colors.backgroundCard)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Edit",
                                style = QuestTheme.typography.bodyMedium.copy(color = QuestTheme.colors.textPrimary)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = QuestTheme.colors.textSecondary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    if (!habit.isDefault) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Delete",
                                    style = QuestTheme.typography.bodyMedium.copy(color = QuestTheme.colors.vitalityRed)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = QuestTheme.colors.vitalityRed
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
