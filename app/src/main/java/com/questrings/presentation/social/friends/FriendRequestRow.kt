package com.questrings.presentation.social.friends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.domain.model.social.FriendRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FriendRequestRow(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDismissed by remember { mutableStateOf(false) }
    var isAccepted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val avatarColor = try {
        Color(android.graphics.Color.parseColor(request.fromAvatarColorHex))
    } catch (_: Exception) {
        RoyalPurple
    }

    AnimatedVisibility(
        visible = !isDismissed,
        exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isAccepted) IntellectGreen.copy(alpha = 0.15f) else BackgroundCard)
                .border(
                    1.dp,
                    if (isAccepted) IntellectGreen else BorderCard,
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                    ) {
                        Text(
                            text = request.fromDisplayName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = request.fromDisplayName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            isAccepted = true
                            scope.launch {
                                delay(250)
                                isDismissed = true
                                onAccept()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IntellectGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(width = 80.dp, height = 34.dp)
                    ) {
                        Text(
                            text = "✓ Accept",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                isDismissed = true
                                onReject()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VitalityRed.copy(alpha = 0.6f)),
                        modifier = Modifier.size(width = 40.dp, height = 34.dp)
                    ) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = VitalityRed
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun FriendRequestRowPreview() {
    QuestRingsTheme {
        FriendRequestRow(
            request = FriendRequest(
                id = "r1",
                fromUserId = "u2",
                fromDisplayName = "Alex",
                fromAvatarColorHex = "#0A84FF",
                sentAt = 0L
            ),
            onAccept = {},
            onReject = {}
        )
    }
}
