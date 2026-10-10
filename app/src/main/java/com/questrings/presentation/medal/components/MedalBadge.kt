package com.questrings.presentation.medal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.domain.model.medal.MedalTier

@Composable
fun MedalBadge(
    tier: MedalTier,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    showGlow: Boolean = true,
    showLabel: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    if (tier == MedalTier.NONE) return

    val tierColor = try {
        Color(android.graphics.Color.parseColor(tier.colorHex))
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }

    val glowColor = try {
        Color(android.graphics.Color.parseColor(tier.glowColorHex))
    } catch (_: Exception) {
        tierColor.copy(alpha = 0.4f)
    }

    val clickableMod = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    if (showLabel) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .then(clickableMod)
                .clip(RoundedCornerShape(16.dp))
                .background(tierColor.copy(alpha = 0.12f))
                .border(1.dp, tierColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = tier.emoji,
                fontSize = (size.value * 0.5f).sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = tier.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = tierColor
            )
        }
    } else {
        val glowMod = if (showGlow) {
            Modifier.shadow(elevation = 6.dp, shape = CircleShape, ambientColor = glowColor, spotColor = glowColor)
        } else {
            Modifier
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .then(clickableMod)
                .then(glowMod)
                .size(size)
                .clip(CircleShape)
                .background(tierColor.copy(alpha = 0.15f))
                .border(1.5.dp, tierColor.copy(alpha = 0.8f), CircleShape)
        ) {
            Text(
                text = tier.emoji,
                fontSize = (size.value * 0.55f).sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111116)
@Composable
fun MedalBadgePreview() {
    QuestRingsTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            MedalBadge(tier = MedalTier.BRONZE)
            Spacer(modifier = Modifier.width(8.dp))
            MedalBadge(tier = MedalTier.SILVER)
            Spacer(modifier = Modifier.width(8.dp))
            MedalBadge(tier = MedalTier.GOLD)
            Spacer(modifier = Modifier.width(8.dp))
            MedalBadge(tier = MedalTier.PLATINUM, showLabel = true)
        }
    }
}
