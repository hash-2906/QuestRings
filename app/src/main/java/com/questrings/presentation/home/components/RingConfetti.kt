package com.questrings.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val color: Color,
    val radius: Float,
    val speedX: Float,
    val speedY: Float,
    val rotation: Float
)

@Composable
fun RingConfetti(
    visible: Boolean,
    onAnimationEnd: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        val particles = remember {
            mutableStateListOf<ConfettiParticle>().apply {
                val colors = listOf(
                    Color(0xFFFF2D55), // Vitality
                    Color(0xFF30D158), // Intellect
                    Color(0xFF0A84FF), // Discipline
                    Color(0xFFFFD60A), // Gold
                    Color(0xFFBF5AF2)  // Purple
                )
                repeat(70) {
                    add(
                        ConfettiParticle(
                            x = Random.nextFloat() * 1000f,
                            y = Random.nextFloat() * 200f,
                            color = colors.random(),
                            radius = Random.nextFloat() * 8f + 6f,
                            speedX = (Random.nextFloat() - 0.5f) * 6f,
                            speedY = Random.nextFloat() * 8f + 5f,
                            rotation = Random.nextFloat() * 360f
                        )
                    )
                }
            }
        }

        LaunchedEffect(visible) {
            if (visible) {
                delay(3000)
                onAnimationEnd()
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                drawRect(
                    color = p.color,
                    topLeft = Offset(p.x % size.width, (p.y + p.speedY * 5f) % size.height),
                    size = Size(p.radius, p.radius * 1.5f)
                )
            }
        }
    }
}
