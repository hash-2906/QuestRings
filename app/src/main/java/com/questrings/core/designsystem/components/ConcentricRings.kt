package com.questrings.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundTrack
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.core.designsystem.theme.VitalityRed

data class RingProgressData(
    val vitalityProgress: Float = 0f,   // 0.0 to 1.0 (or >1.0)
    val intellectProgress: Float = 0f,  // 0.0 to 1.0
    val disciplineProgress: Float = 0f  // 0.0 to 1.0
) {
    val overallPercentage: Int
        get() {
            val total = vitalityProgress + intellectProgress + disciplineProgress
            return (total / 3f * 100f).toInt().coerceIn(0, 100)
        }
}

@Composable
fun ConcentricRings(
    data: RingProgressData,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    strokeWidth: Dp = 16.dp,
    trackColor: Color = BackgroundTrack,
    vitalityColor: Color = VitalityRed,
    intellectColor: Color = IntellectGreen,
    disciplineColor: Color = DisciplineBlue,
    showLegend: Boolean = true,
    showCenterText: Boolean = true
) {
    val animVitality by animateFloatAsState(
        targetValue = data.vitalityProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "VitalityRingAnim"
    )

    val animIntellect by animateFloatAsState(
        targetValue = data.intellectProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1100, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "IntellectRingAnim"
    )

    val animDiscipline by animateFloatAsState(
        targetValue = data.disciplineProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1100, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "DisciplineRingAnim"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = strokeWidth.toPx()
                val centerOffset = Offset(size.toPx() / 2f, size.toPx() / 2f)

                // Radius steps for 3 concentric rings
                // Outer ring
                val outerRadius = (size.toPx() / 2f) - (strokePx / 2f)
                // Middle ring
                val middleRadius = outerRadius - strokePx - (strokePx * 0.35f)
                // Inner ring
                val innerRadius = middleRadius - strokePx - (strokePx * 0.35f)

                val ringConfigs = listOf(
                    Triple(outerRadius, animVitality, vitalityColor),
                    Triple(middleRadius, animIntellect, intellectColor),
                    Triple(innerRadius, animDiscipline, disciplineColor)
                )

                ringConfigs.forEach { (radius, progress, color) ->
                    val diameter = radius * 2
                    val topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                    val arcSize = Size(diameter, diameter)

                    // Draw Background Track
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )

                    // Draw Progress Arc
                    if (progress > 0.005f) {
                        drawArc(
                            color = color,
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // Center slot
            if (showCenterText) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${data.overallPercentage}%",
                        style = QuestTheme.typography.ringCenterPercent,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "COMPLETE",
                        style = QuestTheme.typography.ringCenterKicker,
                        color = TextSecondary
                    )
                }
            }
        }

        // Legend
        if (showLegend) {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RingLegendItem(
                    color = vitalityColor,
                    label = "Vitality",
                    percent = (data.vitalityProgress * 100).toInt()
                )
                RingLegendItem(
                    color = intellectColor,
                    label = "Intellect",
                    percent = (data.intellectProgress * 100).toInt()
                )
                RingLegendItem(
                    color = disciplineColor,
                    label = "Discipline",
                    percent = (data.disciplineProgress * 100).toInt()
                )
            }
        }
    }
}

@Composable
private fun RingLegendItem(
    color: Color,
    label: String,
    percent: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label $percent%",
            style = QuestTheme.typography.monoKicker,
            color = TextSecondary,
            fontSize = 9.sp
        )
    }
}
