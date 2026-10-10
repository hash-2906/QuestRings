package com.questrings.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.theme.BackgroundTrackBar
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextCounter
import com.questrings.core.designsystem.theme.TextLightMuted
import com.questrings.core.designsystem.theme.XpFillGradient
import java.text.NumberFormat
import java.util.Locale

@Composable
fun XpProgressBar(
    currentXp: Int,
    targetXp: Int = 1000,
    modifier: Modifier = Modifier,
    label: String = "Total Experience",
    height: Dp = 7.dp,
    trackColor: Color = BackgroundTrackBar,
    gradient: Brush = XpFillGradient,
    showLabels: Boolean = true
) {
    val progressRatio = (currentXp.toFloat() / targetXp.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "XpProgressAnimation"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        if (showLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = QuestTheme.typography.bodyMedium,
                    color = TextLightMuted
                )

                val formattedCurrent = NumberFormat.getNumberInstance(Locale.US).format(currentXp)
                val formattedTarget = NumberFormat.getNumberInstance(Locale.US).format(targetXp)

                Text(
                    text = "$formattedCurrent / $formattedTarget XP",
                    style = QuestTheme.typography.monoCounter,
                    color = TextCounter
                )
            }
        }

        // Track & Fill Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(10.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(height)
                    .clip(RoundedCornerShape(10.dp))
                    .background(gradient)
            )
        }
    }
}
