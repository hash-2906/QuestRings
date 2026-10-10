package com.questrings.presentation.social.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ALLOWED_EMOJIS = listOf("👏", "🔥", "⚡")

@Composable
fun ReactionRow(
    reactions: Map<String, List<String>>,
    currentUserId: String,
    onToggleReaction: (emoji: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPickerOpen by remember { mutableStateOf(false) }
    val haptics = com.questrings.core.haptics.rememberQuestHaptics()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Render existing reaction chips
        reactions.forEach { (emoji, userIds) ->
            if (userIds.isNotEmpty()) {
                val hasReacted = userIds.contains(currentUserId)
                ReactionChip(
                    emoji = emoji,
                    count = userIds.size,
                    isSelected = hasReacted,
                    onClick = {
                        haptics.lightImpact()
                        onToggleReaction(emoji)
                    }
                )
            }
        }

        // Add reaction [+] button & popover
        Box(contentAlignment = Alignment.CenterStart) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BackgroundCard)
                        .border(1.dp, BorderCard, CircleShape)
                        .clickable {
                            haptics.lightImpact()
                            isPickerOpen = !isPickerOpen
                        }
                ) {
                    Text(
                        text = if (isPickerOpen) "✕" else "+",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextMuted
                    )
                }

                AnimatedVisibility(
                    visible = isPickerOpen,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BackgroundCard)
                            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ALLOWED_EMOJIS.forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        haptics.lightImpact()
                                        onToggleReaction(emoji)
                                        isPickerOpen = false
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReactionChip(
    emoji: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.35f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "reactionBounce"
    )

    val chipBg = if (isSelected) StreakOrange.copy(alpha = 0.2f) else BackgroundCard
    val chipBorder = if (isSelected) StreakOrange.copy(alpha = 0.7f) else BorderCard
    val textColor = if (isSelected) StreakOrange else TextPrimary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(chipBg)
            .border(1.dp, chipBorder, RoundedCornerShape(14.dp))
            .clickable {
                scope.launch {
                    isPressed = true
                    delay(120)
                    isPressed = false
                }
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ReactionRowPreview() {
    QuestRingsTheme {
        ReactionRow(
            reactions = mapOf(
                "👏" to listOf("u1", "u2"),
                "🔥" to listOf("me"),
                "⚡" to emptyList()
            ),
            currentUserId = "me",
            onToggleReaction = {}
        )
    }
}
