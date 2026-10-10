package com.questrings.presentation.rooms.detail.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.domain.model.room.JoinRequest
import com.questrings.domain.model.room.JoinRequestStatus

@Composable
fun JoinRequestCard(
    request: JoinRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarColor = runCatching {
        Color(android.graphics.Color.parseColor(request.avatarColorHex))
    }.getOrDefault(AccentPurple)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.25f))
                        .border(1.5.dp, avatarColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.displayName.firstOrNull()?.uppercase() ?: "A",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.displayName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Requests to join this chamber",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuestButton(
                    text = "REJECT",
                    icon = Icons.Default.Close,
                    variant = QuestButtonVariant.Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = onReject
                )
                QuestButton(
                    text = "APPROVE",
                    icon = Icons.Default.Check,
                    variant = QuestButtonVariant.Primary,
                    modifier = Modifier.weight(1f),
                    onClick = onApprove
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun JoinRequestCardPreview() {
    QuestRingsTheme {
        JoinRequestCard(
            request = JoinRequest(
                id = "req-1",
                roomId = "room-1",
                userId = "user-3",
                displayName = "Trinity",
                avatarColorHex = "#30D158",
                requestedAt = System.currentTimeMillis(),
                status = JoinRequestStatus.PENDING
            ),
            onApprove = {},
            onReject = {}
        )
    }
}
