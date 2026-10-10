package com.questrings.presentation.medal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.config.MedalThreshold
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.xp.MedalUnlockEvent
import com.questrings.presentation.medal.components.MedalGlowRing
import com.questrings.presentation.medal.components.MedalParticleBurst

@Composable
fun MedalUnlockDialog(
    event: MedalUnlockEvent,
    userName: String = "Adventurer",
    avatarColorHex: String = "#7E5DFF",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val tier = event.tier
    val tierName = tier.name.lowercase()

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/medal_$tierName.json")
    )

    val tierColor = try {
        Color(android.graphics.Color.parseColor(tier.colorHex))
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.88f)),
            contentAlignment = Alignment.Center
        ) {
            // Particle burst background
            MedalParticleBurst(
                tier = tier,
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    tierColor.copy(alpha = 0.18f),
                                    BackgroundCard,
                                    BackgroundPrimary
                                )
                            )
                        )
                        .border(1.5.dp, tierColor.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    // Eyebrow label
                    Text(
                        text = "NEW MEDAL ACHIEVED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = tierColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Center Animated Medal with Pulsing Glow Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(180.dp)
                    ) {
                        MedalGlowRing(tier = tier, size = 180.dp)

                        if (composition != null) {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(130.dp)
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(tierColor.copy(alpha = 0.2f))
                                    .border(2.dp, tierColor, CircleShape)
                            ) {
                                Text(
                                    text = tier.emoji,
                                    fontSize = 48.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = event.threshold.title.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = event.threshold.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Threshold Requirement Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(tierColor.copy(alpha = 0.1f))
                            .border(1.dp, tierColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${"%,d".format(event.threshold.requiredXp)} XP  •  LEVEL ${event.threshold.requiredLevel}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tierColor
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Share button
                    OutlinedButton(
                        onClick = {
                            AchievementCardGenerator.shareAchievement(
                                context = context,
                                userName = userName,
                                tier = tier,
                                title = event.threshold.title
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.8f))
                    ) {
                        Text(
                            text = "SHARE ACHIEVEMENT",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = tierColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Claim button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tierColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "CLAIM & CONTINUE",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedalUnlockDialogPreview() {
    QuestRingsTheme {
        MedalUnlockDialog(
            event = MedalUnlockEvent(
                tier = MedalTier.GOLD,
                threshold = MedalThreshold(
                    tier = MedalTier.GOLD,
                    requiredXp = 7500,
                    requiredLevel = 20,
                    title = "Gold Guardian",
                    description = "Few reach this height. You are among them."
                ),
                previousTier = MedalTier.SILVER
            ),
            onDismiss = {}
        )
    }
}
