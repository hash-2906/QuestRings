package com.questrings.presentation.medal.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.questrings.domain.model.medal.MedalTier

@Composable
fun MedalGlowRing(
    tier: MedalTier,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val tierColor = try {
        Color(android.graphics.Color.parseColor(tier.colorHex))
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }

    val glowColor = try {
        Color(android.graphics.Color.parseColor(tier.glowColorHex))
    } catch (_: Exception) {
        tierColor.copy(alpha = 0.5f)
    }

    val transition = rememberInfiniteTransition(label = "MedalGlowRing")

    val pulseScale1 by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale1"
    )

    val pulseAlpha1 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha1"
    )

    val pulseScale2 by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale2"
    )

    val pulseAlpha2 by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha2"
    )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2.5f

            // Core soft halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.5f),
                        glowColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.2f
                ),
                radius = baseRadius * 1.2f,
                center = center
            )

            // Outer pulse wave 1
            drawCircle(
                color = tierColor.copy(alpha = pulseAlpha1 * 0.7f),
                radius = baseRadius * pulseScale1,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Outer pulse wave 2
            drawCircle(
                color = glowColor.copy(alpha = pulseAlpha2 * 0.9f),
                radius = baseRadius * pulseScale2,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Rotating segmented aura
            rotate(rotation, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            tierColor.copy(alpha = 0.6f),
                            Color.Transparent,
                            glowColor.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        center = center
                    ),
                    radius = baseRadius * 0.95f,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MedalGlowRingPreview() {
    MedalGlowRing(tier = MedalTier.GOLD)
}
