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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.User
import com.questrings.domain.model.medal.MedalTier
import com.questrings.presentation.medal.components.MedalBadge

@Composable
fun ProfileHeaderCard(
    user: User?,
    level: Int,
    currentMedal: MedalTier,
    modifier: Modifier = Modifier
) {
    val avatarColor = try {
        Color(android.graphics.Color.parseColor(user?.avatarColorHex ?: "#7E5DFF"))
    } catch (_: Exception) {
        RoyalPurple
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(avatarColor)
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Text(
                        text = (user?.displayName?.take(1) ?: "A").uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.displayName ?: "Cyber Adventurer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user?.email ?: "user@questrings.app",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Level pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoyalPurple.copy(alpha = 0.2f))
                                .border(1.dp, RoyalPurple, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LEVEL $level",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalPurple
                            )
                        }

                        if (currentMedal != MedalTier.NONE) {
                            MedalBadge(
                                tier = currentMedal,
                                showLabel = true,
                                size = 20.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ProfileHeaderCardPreview() {
    QuestRingsTheme {
        ProfileHeaderCard(
            user = User(
                uid = "1",
                email = "player@questrings.io",
                displayName = "NeonDrifter",
                avatarColorHex = "#7E5DFF",
                createdAt = 0L
            ),
            level = 14,
            currentMedal = MedalTier.SILVER
        )
    }
}
