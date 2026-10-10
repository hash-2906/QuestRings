package com.questrings.presentation.profile.public.components

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
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.domain.model.HabitCategory

@Composable
fun RingFocusBreakdown(
    xpByCategory: Map<HabitCategory, Int>,
    modifier: Modifier = Modifier
) {
    val vitalityXp = xpByCategory[HabitCategory.VITALITY] ?: 0
    val intellectXp = xpByCategory[HabitCategory.INTELLECT] ?: 0
    val disciplineXp = xpByCategory[HabitCategory.DISCIPLINE] ?: 0

    val total = (vitalityXp + intellectXp + disciplineXp).coerceAtLeast(1)

    val vPercent = (vitalityXp * 100) / total
    val iPercent = (intellectXp * 100) / total
    val dPercent = (100 - vPercent - iPercent).coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ATTRIBUTE FOCUS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextMuted
            )

            // Multi-segment horizontal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                if (vPercent > 0) {
                    Box(
                        modifier = Modifier
                            .weight(vPercent.toFloat())
                            .height(8.dp)
                            .background(VitalityRed)
                    )
                }
                if (iPercent > 0) {
                    Box(
                        modifier = Modifier
                            .weight(iPercent.toFloat())
                            .height(8.dp)
                            .background(IntellectGreen)
                    )
                }
                if (dPercent > 0) {
                    Box(
                        modifier = Modifier
                            .weight(dPercent.toFloat())
                            .height(8.dp)
                            .background(DisciplineBlue)
                    )
                }
            }

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FocusLegendItem("Vitality", "$vPercent%", VitalityRed)
                FocusLegendItem("Intellect", "$iPercent%", IntellectGreen)
                FocusLegendItem("Discipline", "$dPercent%", DisciplineBlue)
            }
        }
    }
}

@Composable
private fun FocusLegendItem(
    label: String,
    percent: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .height(8.dp)
                .padding(horizontal = 4.dp)
                .background(color)
        )
        Text(
            text = "$label ($percent)",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun RingFocusBreakdownPreview() {
    QuestRingsTheme {
        RingFocusBreakdown(
            xpByCategory = mapOf(
                HabitCategory.VITALITY to 5000,
                HabitCategory.INTELLECT to 3000,
                HabitCategory.DISCIPLINE to 2000
            )
        )
    }
}
