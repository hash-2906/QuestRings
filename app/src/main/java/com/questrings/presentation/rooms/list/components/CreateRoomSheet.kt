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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.theme.QuestTheme
import com.questrings.domain.model.room.RoomPrivacy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onCreate: (
        name: String,
        description: String,
        emoji: String,
        privacy: RoomPrivacy,
        maxMembers: Int
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("⚡") }
    var privacy by remember { mutableStateOf(RoomPrivacy.PUBLIC) }
    var maxMembers by remember { mutableFloatStateOf(50f) }

    val curatedEmojis = listOf(
        "⚡", "🎯", "🔥", "🛡️", "⚔️", "👑",
        "🏋️", "🏃", "🧘", "🧗", "🥊", "🚴",
        "📖", "🧠", "💡", "💻", "🎨", "🔬",
        "🌙", "☀️", "💧", "🌱", "🌌", "🚀",
        "🏆", "💎", "⭐", "🐉", "🐺", "🦅",
        "☕", "🍎", "🥑", "🥗", "🎯", "🧩"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuestTheme.colors.backgroundCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Create a Quest Room",
                style = QuestTheme.typography.heroDisplay.copy(
                    fontSize = 22.sp,
                    color = QuestTheme.colors.textPrimary
                )
            )
            Text(
                text = "Gather your party and conquer goals together.",
                style = QuestTheme.typography.bodySmall.copy(
                    color = QuestTheme.colors.textSecondary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Emoji Selection Header & Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Avatar Slot
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(QuestTheme.colors.backgroundSecondary)
                        .border(1.5.dp, QuestTheme.colors.levelPurple, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = selectedEmoji, fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "ROOM EMBLEM",
                        style = QuestTheme.typography.monoLabel.copy(
                            fontSize = 11.sp,
                            color = QuestTheme.colors.textMuted
                        )
                    )
                    Text(
                        text = "Select an emblem that defines your syndicate.",
                        style = QuestTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = QuestTheme.colors.textSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 6-column emoji picker
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(QuestTheme.colors.backgroundSecondary)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(curatedEmojis) { emoji ->
                    val isSelected = emoji == selectedEmoji
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) QuestTheme.colors.levelPurple.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (isSelected) QuestTheme.colors.levelPurple else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Room Name
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 40) name = it },
                label = { Text("Room Name *") },
                placeholder = { Text("e.g., Cyber Knights Gym Syndicate") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuestTheme.colors.disciplineBlue,
                    unfocusedBorderColor = QuestTheme.colors.borderSubtle,
                    focusedTextColor = QuestTheme.colors.textPrimary,
                    unfocusedTextColor = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 120) description = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("Daily quest guidelines and focus topics") },
                minLines = 2,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuestTheme.colors.disciplineBlue,
                    unfocusedBorderColor = QuestTheme.colors.borderSubtle,
                    focusedTextColor = QuestTheme.colors.textPrimary,
                    unfocusedTextColor = QuestTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Privacy Toggle
            Text(
                text = "ACCESS & PRIVACY",
                style = QuestTheme.typography.monoLabel.copy(
                    fontSize = 11.sp,
                    color = QuestTheme.colors.textMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Public button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (privacy == RoomPrivacy.PUBLIC) QuestTheme.colors.intellectGreen.copy(alpha = 0.15f)
                            else QuestTheme.colors.backgroundSecondary
                        )
                        .border(
                            1.dp,
                            if (privacy == RoomPrivacy.PUBLIC) QuestTheme.colors.intellectGreen
                            else QuestTheme.colors.borderSubtle,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { privacy = RoomPrivacy.PUBLIC }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = if (privacy == RoomPrivacy.PUBLIC) QuestTheme.colors.intellectGreen else QuestTheme.colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PUBLIC",
                                style = QuestTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (privacy == RoomPrivacy.PUBLIC) QuestTheme.colors.intellectGreen else QuestTheme.colors.textSecondary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Instant code access",
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 10.sp,
                                color = QuestTheme.colors.textMuted
                            )
                        )
                    }
                }

                // Private button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (privacy == RoomPrivacy.PRIVATE) QuestTheme.colors.disciplineBlue.copy(alpha = 0.15f)
                            else QuestTheme.colors.backgroundSecondary
                        )
                        .border(
                            1.dp,
                            if (privacy == RoomPrivacy.PRIVATE) QuestTheme.colors.disciplineBlue
                            else QuestTheme.colors.borderSubtle,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { privacy = RoomPrivacy.PRIVATE }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (privacy == RoomPrivacy.PRIVATE) QuestTheme.colors.disciplineBlue else QuestTheme.colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PRIVATE",
                                style = QuestTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (privacy == RoomPrivacy.PRIVATE) QuestTheme.colors.disciplineBlue else QuestTheme.colors.textSecondary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Requires admin approval",
                            style = QuestTheme.typography.monoLabel.copy(
                                fontSize = 10.sp,
                                color = QuestTheme.colors.textMuted
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Capacity Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAXIMUM MEMBERS",
                    style = QuestTheme.typography.monoLabel.copy(
                        fontSize = 11.sp,
                        color = QuestTheme.colors.textMuted
                    )
                )
                Text(
                    text = "${maxMembers.toInt()} members",
                    style = QuestTheme.typography.monoNumber.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestTheme.colors.textPrimary
                    )
                )
            }

            Slider(
                value = maxMembers,
                onValueChange = { maxMembers = it },
                valueRange = 5f..200f,
                steps = 38,
                colors = SliderDefaults.colors(
                    thumbColor = QuestTheme.colors.levelPurple,
                    activeTrackColor = QuestTheme.colors.levelPurple,
                    inactiveTrackColor = QuestTheme.colors.backgroundSecondary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            QuestButton(
                text = "Create Room →",
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(
                            name.trim(),
                            description.trim(),
                            selectedEmoji,
                            privacy,
                            maxMembers.toInt()
                        )
                    }
                },
                enabled = name.isNotBlank(),
                variant = QuestButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
