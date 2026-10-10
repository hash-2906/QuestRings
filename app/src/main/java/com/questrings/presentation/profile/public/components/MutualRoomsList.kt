package com.questrings.presentation.profile.public.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPrivacy

@Composable
fun MutualRoomsList(
    mutualRooms: List<Room>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "MUTUAL CHAMBERS (${mutualRooms.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextMuted
            )

            if (mutualRooms.isEmpty()) {
                Text(
                    text = "No shared rooms found with this adventurer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            } else {
                mutualRooms.forEach { room ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(androidx.compose.ui.graphics.Color(0xFF111116))
                            .border(1.dp, BorderCard, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = room.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = room.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "${room.memberCount} members · ${room.privacy.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MutualRoomsListPreview() {
    QuestRingsTheme {
        MutualRoomsList(
            mutualRooms = listOf(
                Room(
                    id = "r1",
                    name = "Coding Club",
                    description = "Tech quests and habits",
                    emoji = "🎯",
                    privacy = RoomPrivacy.PUBLIC,
                    joinCode = "CODE12",
                    creatorId = "u1",
                    maxMembers = 50,
                    memberCount = 18,
                    activeAssignmentCount = 2,
                    createdAt = 0L,
                    isActive = true
                )
            )
        )
    }
}
