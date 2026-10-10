package com.questrings.presentation.auth.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BrandMarkGradient
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.HeroTextGradient
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.VitalityRed

@Composable
fun AuthScaffold(
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    // Continuous slow rotation for ambient background rings (~60s full cycle)
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundRingsRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing)
        ),
        label = "RingsRotationAngle"
    )

    // Form card enter spring animation
    var startCardAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startCardAnimation = true
    }

    val cardScale by animateFloatAsState(
        targetValue = if (startCardAnimation) 1.0f else 0.94f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "AuthCardScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (startCardAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 400),
        label = "AuthCardAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // LAYER 1: Animated Rotating Rings Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height * 0.38f)
            val strokePx = 16.dp.toPx()

            rotate(degrees = rotationAngle, pivot = centerOffset) {
                // Vitality arc (outer)
                val rOuter = size.width * 0.44f
                drawArc(
                    color = VitalityRed.copy(alpha = 0.16f),
                    startAngle = 30f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - rOuter, centerOffset.y - rOuter),
                    size = Size(rOuter * 2, rOuter * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Intellect arc (middle)
                val rMiddle = rOuter - strokePx - 10.dp.toPx()
                drawArc(
                    color = IntellectGreen.copy(alpha = 0.16f),
                    startAngle = 140f,
                    sweepAngle = 220f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - rMiddle, centerOffset.y - rMiddle),
                    size = Size(rMiddle * 2, rMiddle * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Discipline arc (inner)
                val rInner = rMiddle - strokePx - 10.dp.toPx()
                drawArc(
                    color = DisciplineBlue.copy(alpha = 0.16f),
                    startAngle = 270f,
                    sweepAngle = 200f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - rInner, centerOffset.y - rInner),
                    size = Size(rInner * 2, rInner * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // LAYER 2: Ambient Radial Gradients (Discipline blue top-right, Vitality red bottom-left)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(DisciplineBlue.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(1000f, 200f),
                        radius = 800f
                    )
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(VitalityRed.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(100f, 1800f),
                        radius = 900f
                    )
                )
        )

        // LAYER 3: Foreground Scrollable Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                // Brand Mark Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandMarkGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "QuestRings Brand",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = buildAnnotatedString {
                            append("Quest")
                            withStyle(SpanStyle(color = VitalityRed)) {
                                append("Rings")
                            }
                        },
                        style = QuestTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        ),
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tagline with Playfair Display italic
                Text(
                    text = buildAnnotatedString {
                        append("Make today ")
                        withStyle(
                            SpanStyle(
                                brush = HeroTextGradient,
                                fontFamily = QuestTheme.typography.heroAccent.fontFamily,
                                fontStyle = QuestTheme.typography.heroAccent.fontStyle,
                                fontWeight = QuestTheme.typography.heroAccent.fontWeight,
                                fontSize = 21.sp
                            )
                        ) {
                            append("legendary.")
                        }
                    },
                    style = QuestTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Animated Form Card
                QuestCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(cardScale)
                        .alpha(cardAlpha),
                    shape = RoundedCornerShape(24.dp),
                    glossyHighlight = true,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(22.dp)
                ) {
                    content()
                }
            }

            // Bottom Footer
            if (footer != null) {
                Spacer(modifier = Modifier.height(24.dp))
                footer()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
