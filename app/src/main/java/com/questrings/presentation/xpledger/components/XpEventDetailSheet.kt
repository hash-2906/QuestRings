package com.questrings.presentation.xpledger.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.StreakOrange
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XpEventDetailSheet(
    event: XpEvent,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit
) {
    val exactDate = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault()).format(Date(event.earnedAt))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundCard,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TRANSACTION AUDIT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description.ifBlank { event.source.displayName },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoyalPurple.copy(alpha = 0.2f))
                        .border(1.dp, RoyalPurple, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+${event.totalXp} XP",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = DisciplineBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Breakdown card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundPrimary)
                    .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AuditRow("Source Category", event.source.displayName)
                    AuditRow("Base Reward", "${event.baseXp} XP")
                    if (event.bonusXp > 0) {
                        AuditRow("Bonus Reward", "+${event.bonusXp} XP", highlightColor = StreakOrange)
                    }
                    if (event.multiplier > 1.0f) {
                        AuditRow("Multiplier Applied", "×${"%.2f".format(event.multiplier).trimEnd('0').trimEnd('.')}", highlightColor = StreakOrange)
                    }
                    AuditRow("Timestamp", exactDate)
                    if (!event.sourceEntityTitle.isNullOrBlank()) {
                        AuditRow("Linked Entity", event.sourceEntityTitle)
                    }
                    AuditRow("Cumulative Snapshot", "${"%,d".format(event.cumulativeXpAfter)} XP", highlightColor = DisciplineBlue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalPurple,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "CLOSE",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun AuditRow(
    label: String,
    value: String,
    highlightColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = highlightColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun XpEventDetailSheetPreview() {
    QuestRingsTheme {
        XpEventDetailSheet(
            event = XpEvent(
                id = "1",
                userId = "u1",
                source = XpSource.TASK_COMPLETION,
                baseXp = 100,
                bonusXp = 50,
                totalXp = 150,
                multiplier = 1.5f,
                description = "Boss Quest Cleared",
                sourceEntityId = "t1",
                sourceEntityTitle = "Launch Mobile App",
                earnedAt = System.currentTimeMillis(),
                cumulativeXpAfter = 5400
            ),
            onDismiss = {}
        )
    }
}
