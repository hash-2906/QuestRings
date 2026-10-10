package com.questrings.presentation.rooms.detail.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.AccentPurple
import com.questrings.core.designsystem.theme.BackgroundCard
import com.questrings.core.designsystem.theme.BorderCard
import com.questrings.core.designsystem.theme.BorderSubtle
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.room.RoomMember
import com.questrings.domain.model.room.RoomPermissions
import com.questrings.domain.model.room.RoomRole

@Composable
fun MemberRow(
    member: RoomMember,
    currentUserRole: RoomRole,
    currentUserId: String,
    onPromoteRole: (RoomRole) -> Unit,
    onRemoveMember: () -> Unit,
    onLeaveRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isSelf = member.userId == currentUserId
    val canKick = RoomPermissions.canRemoveMember(currentUserRole, member.role) || isSelf
    val canPromote = currentUserRole == RoomRole.ADMIN && !isSelf

    val avatarColor = runCatching {
        Color(android.graphics.Color.parseColor(member.avatarColorHex))
    }.getOrDefault(AccentPurple)

    val roleBadgeColor = when (member.role) {
        RoomRole.ADMIN -> Color(0xFFFF2D55)
        RoomRole.MODERATOR -> AccentPurple
        RoomRole.MEMBER -> TextSecondary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(avatarColor.copy(alpha = 0.25f))
                    .border(1.5.dp, avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.displayName.firstOrNull()?.uppercase() ?: "A",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isSelf) "${member.displayName} (You)" else member.displayName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Role pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(roleBadgeColor.copy(alpha = 0.15f))
                            .border(1.dp, roleBadgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = member.role.name,
                            color = roleBadgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "${member.totalXpEarned} XP · ${member.assignmentsCompleted} Quests",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Context Menu action
            if (canKick || canPromote || isSelf) {
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Member Actions",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(BackgroundCard)
                    ) {
                        if (canPromote) {
                            if (member.role == RoomRole.MEMBER) {
                                DropdownMenuItem(
                                    text = { Text("Promote to Moderator", color = TextPrimary) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = AccentPurple
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onPromoteRole(RoomRole.MODERATOR)
                                    }
                                )
                            } else if (member.role == RoomRole.MODERATOR) {
                                DropdownMenuItem(
                                    text = { Text("Demote to Member", color = TextPrimary) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = TextSecondary
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onPromoteRole(RoomRole.MEMBER)
                                    }
                                )
                            }
                        }

                        if (!isSelf && canKick) {
                            DropdownMenuItem(
                                text = { Text("Remove Member", color = Color(0xFFFF2D55)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.PersonRemove,
                                        contentDescription = null,
                                        tint = Color(0xFFFF2D55)
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRemoveMember()
                                }
                            )
                        }

                        if (isSelf) {
                            DropdownMenuItem(
                                text = { Text("Leave Room", color = Color(0xFFFF2D55)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.ExitToApp,
                                        contentDescription = null,
                                        tint = Color(0xFFFF2D55)
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onLeaveRoom()
                                }
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
private fun MemberRowPreview() {
    QuestRingsTheme {
        MemberRow(
            member = RoomMember(
                userId = "user-1",
                roomId = "room-1",
                displayName = "Neo",
                avatarColorHex = "#7E5DFF",
                role = RoomRole.ADMIN,
                joinedAt = System.currentTimeMillis(),
                totalXpEarned = 1450,
                assignmentsCompleted = 12
            ),
            currentUserRole = RoomRole.ADMIN,
            currentUserId = "user-2",
            onPromoteRole = {},
            onRemoveMember = {},
            onLeaveRoom = {}
        )
    }
}
