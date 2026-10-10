package com.questrings.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.theme.BackgroundSecondary
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.PrimaryButtonGradient
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary

enum class QuestButtonVariant {
    Primary,
    Secondary,
    Ghost
}

@Composable
fun QuestButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: QuestButtonVariant = QuestButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(10.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 11.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "QuestButtonScale"
    )

    val backgroundModifier = when (variant) {
        QuestButtonVariant.Primary -> {
            if (enabled) {
                Modifier.background(PrimaryButtonGradient, shape)
            } else {
                Modifier.background(BackgroundSecondary.copy(alpha = 0.5f), shape)
            }
        }
        QuestButtonVariant.Secondary -> {
            Modifier
                .background(BackgroundSecondary, shape)
                .border(1.dp, BorderSubtle, shape)
        }
        QuestButtonVariant.Ghost -> {
            Modifier.background(Color.Transparent, shape)
        }
    }

    val textColor = when {
        !enabled -> TextMuted
        variant == QuestButtonVariant.Ghost -> QuestTheme.colors.textSecondary
        else -> TextPrimary
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .then(backgroundModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                enabled = enabled,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                style = QuestTheme.typography.button,
                color = textColor
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(6.dp))
                trailingIcon()
            }
        }
    }
}
