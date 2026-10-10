package com.questrings.presentation.tasks.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.TaskCountdown

@Composable
fun TaskCountdownDisplay(
    countdown: TaskCountdown,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (countdown.isCountingDown || countdown.isInGrace) {
        AnimatedContent(
            targetState = countdown.displayLabel,
            transitionSpec = {
                slideInVertically { height -> height } togetherWith
                        slideOutVertically { height -> -height }
            },
            label = "countdownTicker",
            modifier = modifier
        ) { labelText ->
            Text(
                text = labelText,
                style = QuestTheme.typography.monoNumber.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    } else {
        Text(
            text = countdown.displayLabel,
            style = QuestTheme.typography.monoLabel.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            ),
            modifier = modifier
        )
    }
}
