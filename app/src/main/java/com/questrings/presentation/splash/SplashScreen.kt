package com.questrings.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BrandMarkGradient
import com.questrings.core.designsystem.theme.HeroTextGradient
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun SplashScreen(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnim by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1.0f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "SplashLogoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 700),
        label = "SplashLogoAlpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        val checkSessionStartTime = System.currentTimeMillis()
        val user = try {
            observeCurrentUserUseCase().firstOrNull()
        } catch (_: Exception) {
            null
        }

        // Ensure minimum 1.2s splash duration for smooth aesthetic
        val elapsed = System.currentTimeMillis() - checkSessionStartTime
        val remaining = (1200L - elapsed).coerceAtLeast(400L)
        delay(remaining)

        when {
            user == null -> onNavigateToAuth()
            user.displayName.isBlank() -> onNavigateToProfileSetup()
            else -> onNavigateToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale)
                .alpha(logoAlpha)
        ) {
            // Brand Logo Box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandMarkGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "QuestRings Brand",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = buildAnnotatedString {
                    append("Quest")
                    withStyle(SpanStyle(color = VitalityRed)) {
                        append("Rings")
                    }
                },
                style = QuestTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("Make today ")
                    withStyle(
                        SpanStyle(
                            brush = HeroTextGradient,
                            fontFamily = QuestTheme.typography.heroAccent.fontFamily,
                            fontStyle = QuestTheme.typography.heroAccent.fontStyle,
                            fontWeight = QuestTheme.typography.heroAccent.fontWeight,
                            fontSize = 20.sp
                        )
                    ) {
                        append("legendary.")
                    }
                },
                style = QuestTheme.typography.titleMedium,
                color = TextPrimary
            )
        }
    }
}

@Preview(name = "Splash Screen", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SplashScreenPreview() {
    QuestRingsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BrandMarkGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "QuestRings",
                    style = QuestTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    color = TextPrimary
                )
            }
        }
    }
}

@Preview(name = "Splash Screen - Light Forced", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SplashScreenLightPreview() {
    QuestRingsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BrandMarkGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "QuestRings",
                    style = QuestTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    color = TextPrimary
                )
            }
        }
    }
}

