package com.questrings.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.questrings.core.designsystem.components.ConcentricRings
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.components.RingProgressData
import com.questrings.core.designsystem.theme.QuestTheme

@Composable
fun HeroRingsCard(
    ringData: RingProgressData,
    vitalityCompleted: Int,
    vitalityTotal: Int,
    intellectCompleted: Int,
    intellectTotal: Int,
    disciplineCompleted: Int,
    disciplineTotal: Int,
    modifier: Modifier = Modifier
) {
    QuestCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TODAY'S RINGS",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConcentricRings(
                data = ringData,
                size = 190.dp,
                strokeWidth = 14.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RingMetricItem(
                    name = "Vitality",
                    progress = "${vitalityCompleted}/${vitalityTotal}",
                    percent = "${(ringData.vitalityProgress * 100).toInt()}%",
                    color = QuestTheme.colors.vitalityRed
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(QuestTheme.colors.borderSubtle)
                )

                RingMetricItem(
                    name = "Intellect",
                    progress = "${intellectCompleted}/${intellectTotal}",
                    percent = "${(ringData.intellectProgress * 100).toInt()}%",
                    color = QuestTheme.colors.intellectGreen
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(QuestTheme.colors.borderSubtle)
                )

                RingMetricItem(
                    name = "Discipline",
                    progress = "${disciplineCompleted}/${disciplineTotal}",
                    percent = "${(ringData.disciplineProgress * 100).toInt()}%",
                    color = QuestTheme.colors.disciplineBlue
                )
            }
        }
    }
}

@Composable
private fun RingMetricItem(
    name: String,
    progress: String,
    percent: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = QuestTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = QuestTheme.colors.textSecondary
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = percent,
            style = QuestTheme.typography.monoNumber.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = QuestTheme.colors.textPrimary
            )
        )
        Text(
            text = progress,
            style = QuestTheme.typography.monoLabel.copy(
                fontSize = 11.sp,
                color = QuestTheme.colors.textMuted
            )
        )
    }
}
