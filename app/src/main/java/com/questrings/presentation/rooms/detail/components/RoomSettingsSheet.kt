package com.questrings.presentation.rooms.detail.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.components.QuestButton
import com.questrings.core.designsystem.components.QuestButtonVariant
import com.questrings.core.designsystem.components.QuestTextField
import com.questrings.core.designsystem.theme.AccentOrange
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BackgroundPrimary
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.room.Room
import com.questrings.domain.model.room.RoomPrivacy
import com.questrings.domain.model.room.RoomRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSettingsSheet(
    room: Room,
    userRole: RoomRole,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSaveSettings: (name: String, description: String, privacy: RoomPrivacy) -> Unit,
    onRegenerateCode: () -> Unit,
    onDeleteRoom: () -> Unit
) {
    var name by remember(room) { mutableStateOf(room.name) }
    var description by remember(room) { mutableStateOf(room.description) }
    var privacy by remember(room) { mutableStateOf(room.privacy) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundPrimary,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "ROOM SETTINGS",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Room Name
            Text(
                text = "Room Name",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            QuestTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Room Name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Room Description
            Text(
                text = "Description",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            QuestTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Briefly describe the room purpose..."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Toggle
            Text(
                text = "Access Privacy",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Public option
                val isPub = privacy == RoomPrivacy.PUBLIC
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isPub) AccentPurple.copy(alpha = 0.2f) else BackgroundCard)
                        .border(
                            width = 1.5.dp,
                            color = if (isPub) AccentPurple else BorderCard,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { privacy = RoomPrivacy.PUBLIC }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = if (isPub) AccentPurple else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "PUBLIC",
                            color = if (isPub) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Private option
                val isPriv = privacy == RoomPrivacy.PRIVATE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isPriv) AccentOrange.copy(alpha = 0.2f) else BackgroundCard)
                        .border(
                            width = 1.5.dp,
                            color = if (isPriv) AccentOrange else BorderCard,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { privacy = RoomPrivacy.PRIVATE }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isPriv) AccentOrange else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "PRIVATE",
                            color = if (isPriv) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Regenerate Code Button
            QuestButton(
                text = "Regenerate Join Code",
                icon = Icons.Default.Refresh,
                variant = QuestButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
                onClick = onRegenerateCode
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Save Settings
            QuestButton(
                text = "SAVE CHANGES",
                variant = QuestButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank(),
                onClick = {
                    onSaveSettings(name.trim(), description.trim(), privacy)
                }
            )

            // Admin Danger Zone
            if (userRole == RoomRole.ADMIN) {
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFF2D55).copy(alpha = 0.08f))
                        .border(1.dp, Color(0xFFFF2D55).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "DANGER ZONE",
                            color = Color(0xFFFF2D55),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Deleting this room will remove all active assignments, XP contributions, and kick all members permanently.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        QuestButton(
                            text = if (showDeleteConfirm) "CONFIRM DELETE ROOM" else "DELETE ROOM",
                            icon = Icons.Default.DeleteForever,
                            variant = QuestButtonVariant.Danger,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (showDeleteConfirm) {
                                    onDeleteRoom()
                                } else {
                                    showDeleteConfirm = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
