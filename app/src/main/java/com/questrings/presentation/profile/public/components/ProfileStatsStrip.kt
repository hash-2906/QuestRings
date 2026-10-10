package com.questrings.presentation.profile.public.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary

@Composable
fun ProfileStatsStrip(
    level: Int,
    streak: Int,
    totalXp: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                label = "LEVEL",
                value = "$level",
                highlightColor = TextPrimary
            )
            StatItem(
                label = "STREAK",
                value = "🔥 $streak",
                highlightColor = StreakOrange
            )
            StatItem(
                label = "CAREER XP",
                value = "⚡ ${"%,d".format(totalXp)}",
                highlightColor = DisciplineBlue
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    highlightColor: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = highlightColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
            color = TextMuted
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ProfileStatsStripPreview() {
    QuestRingsTheme {
        ProfileStatsStrip(level = 24, streak = 12, totalXp = 12450)
    }
}
