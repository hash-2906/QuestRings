package com.questrings.presentation.assignment.detail.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.ChecklistItem

@Composable
fun ChecklistCompletionCard(
    items: List<ChecklistItem>,
    checkedIds: Set<String>,
    onToggleItem: (String) -> Unit,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allChecked = items.isNotEmpty() && checkedIds.containsAll(items.map { it.id })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Complete all items to submit:",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${checkedIds.size}/${items.size} done",
                    color = if (allChecked) AccentGreen else AccentPurple,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.sortedBy { it.sortOrder }.forEach { item ->
                    val isChecked = checkedIds.contains(item.id)
                    val boxBorderColor by animateColorAsState(
                        targetValue = if (isChecked) AccentPurple else BorderSubtle,
                        label = "checkboxBorder"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChecked) AccentPurple.copy(alpha = 0.08f) else Color.Transparent)
                            .border(1.dp, if (isChecked) AccentPurple.copy(alpha = 0.25f) else BorderCard, RoundedCornerShape(12.dp))
                            .clickable { onToggleItem(item.id) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Custom Checkbox Box
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isChecked) AccentPurple else Color.Transparent)
                                .border(1.5.dp, boxBorderColor, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = item.label,
                            color = if (isChecked) TextMuted else TextPrimary,
                            fontSize = 14.sp,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            QuestButton(
                text = if (allChecked) "SUBMIT COMPLETION" else "COMPLETE ALL ITEMS TO SUBMIT",
                variant = QuestButtonVariant.Primary,
                enabled = allChecked && !isSubmitting,
                isLoading = isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                onClick = onSubmit
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ChecklistCompletionCardPreview() {
    QuestRingsTheme {
        ChecklistCompletionCard(
            items = listOf(
                ChecklistItem("1", "Read instructions carefully", 0),
                ChecklistItem("2", "Implement domain repository", 1),
                ChecklistItem("3", "Write unit test suite", 2)
            ),
            checkedIds = setOf("1", "2"),
            onToggleItem = {},
            isSubmitting = false,
            onSubmit = {}
        )
    }
}
