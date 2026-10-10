package com.questrings.presentation.profile.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundTrackBar
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.config.MedalThreshold
import com.questrings.domain.model.medal.MedalTier

@Composable
fun MedalProgressCard(
    currentXp: Int,
    currentLevel: Int,
    nextThreshold: MedalThreshold?,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        if (nextThreshold != null) {
            val tier = nextThreshold.tier
            val tierColor = try {
                Color(android.graphics.Color.parseColor(tier.colorHex))
            } catch (_: Exception) {
                Color(0xFFFFD700)
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NEXT MEDAL TARGET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${nextThreshold.title.uppercase()} (${tier.emoji})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tierColor
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(tierColor.copy(alpha = 0.15f))
                            .border(1.dp, tierColor, CircleShape)
                    ) {
                        Text(text = tier.emoji, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = tierColor,
                    trackColor = BackgroundTrackBar
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "XP PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                        Text(
                            text = "${"%,d".format(currentXp)} / ${"%,d".format(nextThreshold.requiredXp)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "LEVEL REQUIRED",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                        Text(
                            text = "Level $currentLevel / ${nextThreshold.requiredLevel}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (currentLevel >= nextThreshold.requiredLevel) Color(0xFF30D158) else TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "“${nextThreshold.description}”",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = TextSecondary
                )
            }
        } else {
            // Apex / Obsidian reached!
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(text = "👑", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SUMMIT ACHIEVED",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "You have unlocked all tiers and reached legendary status.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MedalProgressCardPreview() {
    QuestRingsTheme {
        MedalProgressCard(
            currentXp = 4850,
            currentLevel = 14,
            nextThreshold = MedalThreshold(
                tier = MedalTier.GOLD,
                requiredXp = 7500,
                requiredLevel = 20,
                title = "Gold Guardian",
                description = "Few reach this height. You are among them."
            ),
            progress = 0.65f
        )
    }
}
