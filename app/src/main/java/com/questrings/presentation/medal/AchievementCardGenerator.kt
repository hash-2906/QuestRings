package com.questrings.presentation.medal

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.medal.MedalTier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AchievementCardGenerator {

    fun shareAchievement(
        context: Context,
        userName: String,
        tier: MedalTier,
        title: String
    ) {
        val shareText = """
            ⚔️ I just unlocked the ${tier.emoji} $title medal on QuestRings!
            
            Level up your daily quests with me! 🚀
            #QuestRings #Productivity #RPG
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Achievement")
        context.startActivity(shareIntent)
    }
}

@Composable
fun AchievementCard(
    userName: String,
    avatarColorHex: String,
    tier: MedalTier,
    medalTitle: String,
    medalDescription: String,
    level: Int,
    totalXp: Int,
    modifier: Modifier = Modifier
) {
    val tierColor = try {
        Color(android.graphics.Color.parseColor(tier.colorHex))
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }

    val userAvatarColor = try {
        Color(android.graphics.Color.parseColor(avatarColorHex))
    } catch (_: Exception) {
        Color(0xFF7E5DFF)
    }

    val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        tierColor.copy(alpha = 0.15f),
                        BackgroundCard,
                        BackgroundPrimary
                    )
                )
            )
            .border(1.5.dp, tierColor.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header: App watermark & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUESTRINGS · ACHIEVEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = tierColor
                )
                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Big Medal Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(tierColor.copy(alpha = 0.2f))
                    .border(2.dp, tierColor, CircleShape)
            ) {
                Text(
                    text = tier.emoji,
                    fontSize = 44.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = medalTitle.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = medalDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // User Info Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundPrimary.copy(alpha = 0.6f))
                    .border(1.dp, BorderCard, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(userAvatarColor)
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Level $level Adventurer",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${"%,d".format(totalXp)} XP",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = tierColor
                        )
                        Text(
                            text = "TOTAL REPUTATION",
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
fun AchievementCardPreview() {
    QuestRingsTheme {
        AchievementCard(
            userName = "CyberKnight",
            avatarColorHex = "#7E5DFF",
            tier = MedalTier.GOLD,
            medalTitle = "Gold Guardian",
            medalDescription = "Few reach this height. You are among them.",
            level = 20,
            totalXp = 8240,
            modifier = Modifier.padding(16.dp)
        )
    }
}
