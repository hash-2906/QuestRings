package com.questrings.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundSecondary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary

@Composable
fun QuestChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    textColor: Color = TextPrimary,
    backgroundColor: Color = Color(0x0AFFFFFF),
    borderColor: Color = Color(0x16FFFFFF),
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(20.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = Color.White.copy(alpha = 0.2f)),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
            .then(clickableModifier)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = text,
                style = QuestTheme.typography.monoKicker,
                color = textColor,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun CategoryTab(
    title: String,
    completedCount: Int,
    totalCount: Int,
    categoryColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val shape = RoundedCornerShape(10.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) categoryColor.copy(alpha = 0.55f) else BorderSubtle,
        label = "CategoryTabBorder"
    )
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) categoryColor.copy(alpha = 0.12f) else BackgroundSecondary,
        label = "CategoryTabBg"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) TextPrimary else TextSecondary,
        label = "CategoryTabText"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(animatedBgColor, shape)
            .border(1.dp, animatedBorderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = categoryColor.copy(alpha = 0.2f)),
                onClick = onSelect
            )
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) categoryColor else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = title,
                style = QuestTheme.typography.titleSmall,
                color = animatedTextColor,
                fontSize = 12.sp
            )

            Text(
                text = "$completedCount/$totalCount",
                style = QuestTheme.typography.monoCounter,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
