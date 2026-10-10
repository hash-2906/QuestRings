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
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.domain.config.MedalProgressionConfig
import com.questrings.domain.model.medal.MedalTier

@Composable
fun MedalTimelineRow(
    currentMedal: MedalTier,
    modifier: Modifier = Modifier
) {
    val tiers = listOf(
        MedalTier.BRONZE,
        MedalTier.SILVER,
        MedalTier.GOLD,
        MedalTier.PLATINUM,
        MedalTier.OBSIDIAN
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "MEDAL TIMELINE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tiers.forEach { tier ->
                    val isUnlocked = tier.ordinal <= currentMedal.ordinal && currentMedal != MedalTier.NONE
                    val isCurrent = tier == currentMedal

                    val tierColor = try {
                        Color(android.graphics.Color.parseColor(tier.colorHex))
                    } catch (_: Exception) {
                        Color(0xFFFFD700)
                    }

                    val threshold = MedalProgressionConfig.thresholds.firstOrNull { it.tier == tier }
                    val reqLevel = threshold?.requiredLevel ?: 1

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Node circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isUnlocked) tierColor.copy(alpha = 0.2f)
                                    else Color(0xFF1E1E24)
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isUnlocked) tierColor else Color(0xFF33333D),
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                text = if (isUnlocked) tier.emoji else "🔒",
                                fontSize = if (isUnlocked) 20.sp else 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = tier.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isUnlocked) tierColor else TextMuted
                        )

                        Text(
                            text = "Lv. $reqLevel",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MedalTimelineRowPreview() {
    QuestRingsTheme {
        MedalTimelineRow(currentMedal = MedalTier.SILVER)
    }
}
