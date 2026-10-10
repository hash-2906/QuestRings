package com.questrings.presentation.medal.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.questrings.domain.model.medal.MedalTier
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val isDiamond: Boolean
)

@Composable
fun MedalParticleBurst(
    tier: MedalTier,
    modifier: Modifier = Modifier,
    particleCount: Int = 48
) {
    val tierColor = try {
        Color(android.graphics.Color.parseColor(tier.colorHex))
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }

    val progress = remember { Animatable(0f) }

    val particles = remember(tier) {
        val colors = listOf(
            tierColor,
            tierColor.copy(alpha = 0.8f),
            Color.White,
            Color(0xFFFFD700),
            Color(0xFFFF8B35)
        )
        val rnd = Random(System.currentTimeMillis())
        List(particleCount) {
            Particle(
                angle = rnd.nextFloat() * (2f * Math.PI.toFloat()),
                speed = rnd.nextFloat() * 280f + 120f,
                size = rnd.nextFloat() * 6f + 3f,
                color = colors[rnd.nextInt(colors.size)],
                isDiamond = rnd.nextBoolean()
            )
        }
    }

    LaunchedEffect(tier) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val currentProgress = progress.value
        val alpha = (1f - currentProgress).coerceIn(0f, 1f)

        particles.forEach { p ->
            val dist = p.speed * currentProgress
            val gravity = currentProgress * currentProgress * 80f
            val x = center.x + cos(p.angle) * dist
            val y = center.y + sin(p.angle) * dist + gravity
            val particleAlpha = (alpha * (1f - currentProgress * 0.3f)).coerceIn(0f, 1f)
            val renderColor = p.color.copy(alpha = particleAlpha)

            if (p.isDiamond) {
                val dSize = p.size * (1f - currentProgress * 0.4f)
                val path = Path().apply {
                    moveTo(x, y - dSize)
                    lineTo(x + dSize, y)
                    lineTo(x, y + dSize)
                    lineTo(x - dSize, y)
                    close()
                }
                drawPath(path = path, color = renderColor)
            } else {
                drawCircle(
                    color = renderColor,
                    radius = p.size * (1f - currentProgress * 0.4f),
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MedalParticleBurstPreview() {
    MedalParticleBurst(tier = MedalTier.GOLD)
}
