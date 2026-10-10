package com.questrings.presentation.xpledger.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.presentation.xpledger.DayXpStat

@Composable
fun XpWeeklySummaryCard(
    weeklyXpTotal: Int,
    weeklyStats: List<DayXpStat>,
    modifier: Modifier = Modifier
) {
    val maxXp = (weeklyStats.maxOfOrNull { it.xp } ?: 1).coerceAtLeast(100)
    val avgXp = if (weeklyStats.isNotEmpty()) weeklyXpTotal / weeklyStats.size else 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "WEEKLY GAINS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${"%,d".format(weeklyXpTotal)} XP",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = DisciplineBlue
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoyalPurple.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Avg ~$avgXp / day",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = RoyalPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 7-day mini bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyStats.forEach { stat ->
                    val heightFraction = (stat.xp.toFloat() / maxXp.toFloat()).coerceIn(0.08f, 1f)
                    val barBrush = if (stat.isToday) {
                        Brush.verticalGradient(listOf(Color(0xFFFF8B35), Color(0xFFFF2D55)))
                    } else {
                        Brush.verticalGradient(listOf(RoyalPurple, DisciplineBlue))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        // Value tip on today
                        if (stat.isToday) {
                            Text(
                                text = "${stat.xp}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFFF8B35)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bar
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height((60 * heightFraction).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(barBrush)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Day label
                        Text(
                            text = stat.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (stat.isToday) TextPrimary else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun XpWeeklySummaryCardPreview() {
    QuestRingsTheme {
        XpWeeklySummaryCard(
            weeklyXpTotal = 1450,
            weeklyStats = listOf(
                DayXpStat("M", "2026-10-01", 120, false),
                DayXpStat("T", "2026-10-02", 240, false),
                DayXpStat("W", "2026-10-03", 80, false),
                DayXpStat("T", "2026-10-04", 320, false),
                DayXpStat("F", "2026-10-05", 210, false),
                DayXpStat("S", "2026-10-06", 180, false),
                DayXpStat("S", "2026-10-07", 300, true)
            )
        )
    }
}
