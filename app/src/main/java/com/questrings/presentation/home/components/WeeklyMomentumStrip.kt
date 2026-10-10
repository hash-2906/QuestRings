package com.questrings.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayMomentum(
    val dateKey: String, // "YYYY-MM-DD"
    val dayLetter: String, // "M", "T", "W"...
    val dayNumber: String, // "12"
    val completionPercent: Int, // 0..100
    val isToday: Boolean
)

@Composable
fun WeeklyMomentumStrip(
    momentumList: List<DayMomentum>,
    onDayClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(QuestTheme.colors.backgroundCard)
            .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        momentumList.forEach { day ->
            val ringFillColor = when {
                day.completionPercent >= 100 -> QuestTheme.colors.intellectGreen
                day.completionPercent >= 50 -> QuestTheme.colors.streakOrange
                day.completionPercent > 0 -> QuestTheme.colors.disciplineBlue
                else -> QuestTheme.colors.borderSubtle
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onDayClick(day.dateKey) }
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = day.dayLetter,
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = if (day.isToday) QuestTheme.colors.textPrimary else QuestTheme.colors.textMuted,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.isToday) QuestTheme.colors.backgroundSecondary
                            else Color.Transparent
                        )
                        .border(
                            width = if (day.isToday) 2.dp else 1.5.dp,
                            color = ringFillColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.dayNumber,
                        style = QuestTheme.typography.monoNumber.copy(
                            fontSize = 12.sp,
                            color = if (day.isToday) QuestTheme.colors.textPrimary else QuestTheme.colors.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
