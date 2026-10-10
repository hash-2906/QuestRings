package com.questrings.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundSecondary
import com.questrings.core.designsystem.theme.BorderCard

@Composable
fun QuestCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundSecondary,
    borderColor: Color = BorderCard,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(18.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    glossyHighlight: Boolean = false,
    radialGlowColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = Color.White.copy(alpha = 0.1f)),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .border(borderWidth, borderColor, shape)
            .then(clickableModifier)
    ) {
        // Optional glossy top highlight gradient
        if (glossyHighlight) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Optional radial glow
        if (radialGlowColor != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                radialGlowColor.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
