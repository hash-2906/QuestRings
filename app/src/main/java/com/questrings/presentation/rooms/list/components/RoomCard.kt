package com.questrings.presentation.rooms.list.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.model.room.RoomRole

@Composable
fun RoomCard(
    room: Room,
    userRole: RoomRole? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    QuestCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room Avatar Emoji in rounded card
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E24))
                    .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = room.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.name,
                        style = QuestTheme.typography.headingMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestTheme.colors.textPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (userRole != null) {
                        val (roleColor, roleLabel) = when (userRole) {
                            RoomRole.ADMIN -> Pair(QuestTheme.colors.vitalityRed, "ADMIN")
                            RoomRole.MODERATOR -> Pair(QuestTheme.colors.streakOrange, "MOD")
                            RoomRole.MEMBER -> Pair(QuestTheme.colors.textMuted, "MEMBER")
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(roleColor.copy(alpha = 0.15f))
                                .border(1.dp, roleColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = roleLabel,
                                style = QuestTheme.typography.monoLabel.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = roleColor
                                )
                            )
                        }
                    }
                }

                if (room.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = room.description,
                        style = QuestTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = QuestTheme.colors.textSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats & Privacy Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "👥 ${room.memberCount}/${room.maxMembers} members",
                        style = QuestTheme.typography.monoLabel.copy(
                            fontSize = 11.sp,
                            color = QuestTheme.colors.textSecondary
                        )
                    )

                    Text(
                        text = "• 📋 ${room.activeAssignmentCount} active",
                        style = QuestTheme.typography.monoLabel.copy(
                            fontSize = 11.sp,
                            color = QuestTheme.colors.textSecondary
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Privacy Pill
                    val isPrivate = room.privacy == RoomPrivacy.PRIVATE
                    val privacyColor = if (isPrivate) QuestTheme.colors.disciplineBlue else QuestTheme.colors.intellectGreen
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(privacyColor.copy(alpha = 0.15f))
                            .border(1.dp, privacyColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPrivate) Icons.Default.Lock else Icons.Default.Public,
                            contentDescription = null,
                            tint = privacyColor,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isPrivate) "PRIVATE" else "PUBLIC",
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = privacyColor
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RoomCardPreview() {
    QuestRingsTheme {
        RoomCard(
            room = Room(
                id = "1",
                name = "Deep Work Syndicate",
                description = "Daily 2-hour distraction-free focus sessions.",
                emoji = "⚡",
                privacy = RoomPrivacy.PUBLIC,
                joinCode = "X7K2QR",
                maxMembers = 50,
                memberCount = 28,
                activeAssignmentCount = 3
            ),
            userRole = RoomRole.ADMIN,
            onClick = {}
        )
    }
}
