package com.questrings.presentation.rooms.detail.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.AccentGreen
import com.questrings.core.designsystem.theme.AccentOrange
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPermissions
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.model.room.RoomRole

@Composable
fun RoomHeaderCard(
    room: Room,
    userRole: RoomRole,
    onOpenShare: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPrivate = room.privacy == RoomPrivacy.PRIVATE
    val progress = (room.memberCount.toFloat() / room.maxMembers.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Emoji Icon Box
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentPurple.copy(alpha = 0.15f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = room.emoji.ifBlank { "⚔️" },
                        fontSize = 28.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Privacy Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isPrivate) AccentOrange.copy(alpha = 0.15f)
                                else AccentGreen.copy(alpha = 0.15f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isPrivate) AccentOrange.copy(alpha = 0.4f)
                                else AccentGreen.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isPrivate) Icons.Default.Lock else Icons.Default.Public,
                                contentDescription = null,
                                tint = if (isPrivate) AccentOrange else AccentGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isPrivate) "PRIVATE" else "PUBLIC",
                                color = if (isPrivate) AccentOrange else AccentGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // QR / Share Icon Button
                    IconButton(
                        onClick = onOpenShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Share Room",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings Button (if Admin or Mod)
                    if (RoomPermissions.canManageSettings(userRole)) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Room Settings",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Room Name & Description
            Text(
                text = room.name,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            if (room.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = room.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Capacity indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEMBER CAPACITY",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${room.memberCount} / ${room.maxMembers} Members",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 1f) Color(0xFFFF2D55) else AccentPurple,
                trackColor = BorderSubtle
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Join Code Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderSubtle.copy(alpha = 0.5f))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Quest Room Code", room.joinCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied ${room.joinCode} to clipboard", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "JOIN CODE:",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = room.joinCode,
                    color = AccentPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Code",
                    tint = TextMuted,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RoomHeaderCardPreview() {
    QuestRingsTheme {
        RoomHeaderCard(
            room = Room(
                id = "room-1",
                name = "Cyber Valkyries",
                description = "Daily grinders pursuing elite physical & intellect mastery together.",
                emoji = "⚡",
                privacy = RoomPrivacy.PUBLIC,
                joinCode = "VALK99",
                creatorId = "user-1",
                maxMembers = 50,
                memberCount = 28,
                activeAssignmentCount = 4,
                createdAt = System.currentTimeMillis(),
                isActive = true
            ),
            userRole = RoomRole.ADMIN,
            onOpenShare = {},
            onOpenSettings = {}
        )
    }
}
