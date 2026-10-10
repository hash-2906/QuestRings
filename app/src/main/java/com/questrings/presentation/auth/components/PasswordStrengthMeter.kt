package com.questrings.presentation.auth.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundTrackBar
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.presentation.auth.PasswordStrength

@Composable
fun PasswordStrengthMeter(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    if (strength == PasswordStrength.NONE) return

    val activeCount = when (strength) {
        PasswordStrength.NONE -> 0
        PasswordStrength.WEAK -> 1
        PasswordStrength.MEDIUM -> 2
        PasswordStrength.STRONG -> 3
    }

    val targetColor = when (strength) {
        PasswordStrength.NONE -> BackgroundTrackBar
        PasswordStrength.WEAK -> VitalityRed
        PasswordStrength.MEDIUM -> StreakOrange
        PasswordStrength.STRONG -> IntellectGreen
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 350),
        label = "PasswordStrengthColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 1..3) {
                val isFilled = i <= activeCount
                val segmentColor = if (isFilled) animatedColor else BackgroundTrackBar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(segmentColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strength.name,
                style = QuestTheme.typography.monoKicker,
                color = animatedColor,
                fontSize = 9.sp
            )
        }
    }
}

@Preview(name = "Strength Meter - Medium", showBackground = true, backgroundColor = 0xFF111116)
@Composable
private fun PasswordStrengthMeterPreview() {
    QuestRingsTheme {
        PasswordStrengthMeter(strength = PasswordStrength.MEDIUM)
    }
}
