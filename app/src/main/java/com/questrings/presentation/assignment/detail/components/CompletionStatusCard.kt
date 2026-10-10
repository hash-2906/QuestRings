package com.questrings.presentation.assignment.detail.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentOrange
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.assignment.AssignmentCompletion
import com.questrings.domain.model.assignment.CompletionVerificationStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompletionStatusCard(
    completion: AssignmentCompletion,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d · h:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(completion.completedAt))

    when (completion.verificationStatus) {
        CompletionVerificationStatus.PENDING -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundCard)
                    .border(1.5.dp, AccentOrange.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "SUBMITTED — AWAITING VERIFICATION",
                            color = AccentOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "An Admin or Moderator will review your submission. Points and rank will update once verified.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Submitted: $dateStr",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        CompletionVerificationStatus.APPROVED,
        CompletionVerificationStatus.AUTO_APPROVED -> {
            // Gold shimmer transition
            val transition = rememberInfiniteTransition(label = "shimmer")
            val offset by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "goldShimmer"
            )

            val goldBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFFA500),
                    Color(0xFFFFE4B5),
                    Color(0xFFFFD700)
                ),
                startX = offset,
                endX = offset + 500f
            )

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundCard)
                    .border(1.5.dp, goldBrush, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "QUEST COMPLETED!",
                                    color = Color(0xFFFFD700),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Rank #${completion.rank ?: 1}",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentGreen.copy(alpha = 0.15f))
                                .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "+${completion.xpAwarded ?: 150} XP",
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val speedBonus = completion.speedBonusAwarded ?: 0
                    val totalPoints = completion.pointsAwarded ?: 100
                    val basePoints = (totalPoints - speedBonus).coerceAtLeast(0)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (speedBonus > 0)
                                "$basePoints base + $speedBonus speed bonus = $totalPoints pts"
                            else
                                "$totalPoints pts awarded",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dateStr,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        CompletionVerificationStatus.REJECTED -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundCard)
                    .border(1.5.dp, Color(0xFFFF2D55).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color(0xFFFF2D55),
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "COMPLETION NOT APPROVED",
                            color = Color(0xFFFF2D55),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = completion.rejectionReason ?: "Your submission did not meet verification criteria.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CompletionStatusCardPreview() {
    QuestRingsTheme {
        CompletionStatusCard(
            completion = AssignmentCompletion(
                id = "comp-1",
                assignmentId = "asg-1",
                roomId = "room-1",
                userId = "user-1",
                displayName = "ShadowKnight",
                avatarColorHex = "#7E5DFF",
                completedAt = System.currentTimeMillis(),
                verificationStatus = CompletionVerificationStatus.APPROVED,
                rank = 1,
                pointsAwarded = 125,
                xpAwarded = 187,
                speedBonusAwarded = 25
            )
        )
    }
}
