package com.questrings.presentation.auth.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.QuestTheme

val AvatarColorPresets = listOf(
    "#FF2D55", // Red (Vitality)
    "#30D158", // Green (Intellect)
    "#0A84FF", // Blue (Discipline)
    "#7E5DFF", // Royal Purple
    "#FF8B35", // Fire Orange
    "#00D2D3"  // Neon Teal
)

@Composable
fun AvatarColorPicker(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit,
    displayName: String,
    modifier: Modifier = Modifier
) {
    val selectedColor = try {
        Color(android.graphics.Color.parseColor(selectedColorHex))
    } catch (_: Exception) {
        Color(0xFF7E5DFF)
    }

    // Extract first 1 or 2 initials from display name
    val initials = remember(displayName) {
        val parts = displayName.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        when {
            parts.isEmpty() -> "Q"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0].first()}${parts[1].first()}".uppercase()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Live Avatar Preview
        Box(
            modifier = Modifier
                .size(92.dp)
                .shadow(elevation = 16.dp, shape = CircleShape, ambientColor = selectedColor, spotColor = selectedColor)
                .clip(CircleShape)
                .background(selectedColor)
                .border(3.dp, Color.White.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = QuestTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "CHOOSE AVATAR COLOR",
            style = QuestTheme.typography.monoKicker
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Row of 6 Color Swatches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarColorPresets.forEach { hex ->
                val isSelected = hex.equals(selectedColorHex, ignoreCase = true)
                val color = Color(android.graphics.Color.parseColor(hex))

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.22f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
                    label = "AvatarColorScale"
                )

                val ringPadding by animateDpAsState(
                    targetValue = if (isSelected) 3.dp else 0.dp,
                    animationSpec = spring(dampingRatio = 0.75f),
                    label = "AvatarRingPadding"
                )

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .scale(scale)
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, Color.White, CircleShape)
                            } else Modifier
                        )
                        .padding(ringPadding)
                        .clip(CircleShape)
                        .background(color)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = false, radius = 20.dp),
                            onClick = { onColorSelected(hex) }
                        )
                )
            }
        }
    }
}

@Preview(name = "Avatar Color Picker", showBackground = true, backgroundColor = 0xFF111116)
@Composable
private fun AvatarColorPickerPreview() {
    QuestRingsTheme {
        AvatarColorPicker(
            selectedColorHex = "#7E5DFF",
            onColorSelected = {},
            displayName = "Shadow Knight"
        )
    }
}
