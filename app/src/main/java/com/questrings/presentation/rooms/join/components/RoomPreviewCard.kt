package com.questrings.presentation.rooms.join.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.components.QuestCard
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPrivacy

@Composable
fun RoomPreviewCard(
    room: Room,
    isAlreadyMember: Boolean,
    onJoinOrEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFull = room.memberCount >= room.maxMembers
    val isPrivate = room.privacy == RoomPrivacy.PRIVATE

    QuestCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E1E24))
                        .border(1.dp, QuestTheme.colors.borderSubtle, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = room.emoji, fontSize = 30.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.name,
                        style = QuestTheme.typography.headingMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestTheme.colors.textPrimary
                        )
                    )

                    if (room.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = room.description,
                            style = QuestTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = QuestTheme.colors.textSecondary
                            ),
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "👥 ${room.memberCount}/${room.maxMembers} members",
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                color = QuestTheme.colors.textSecondary
                            )
                        )

                        val badgeColor = if (isPrivate) QuestTheme.colors.disciplineBlue else QuestTheme.colors.intellectGreen
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPrivate) Icons.Default.Lock else Icons.Default.Public,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isPrivate) "PRIVATE" else "PUBLIC",
                                style = QuestTheme.typography.monoLabel.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when {
                isAlreadyMember -> {
                    QuestButton(
                        text = "You're already in this room · Enter →",
                        onClick = onJoinOrEnter,
                        variant = QuestButtonVariant.Secondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                isFull -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(QuestTheme.colors.vitalityRed.copy(alpha = 0.1f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "This room is currently full (${room.maxMembers}/${room.maxMembers})",
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 11.sp,
                                color = QuestTheme.colors.vitalityRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                else -> {
                    QuestButton(
                        text = if (isPrivate) "Request to Join Room" else "Join Room Instantly →",
                        onClick = onJoinOrEnter,
                        variant = QuestButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
