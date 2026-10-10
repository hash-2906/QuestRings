package com.questrings.presentation.social.friends

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.RoyalPurple
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextPrimary
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.core.designsystem.theme.VitalityRed
import com.questrings.domain.model.social.FriendRequest
import com.questrings.domain.model.social.PublicProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsManagerSheet(
    friends: List<PublicProfile>,
    pendingRequests: List<FriendRequest>,
    searchQuery: String,
    searchResults: List<PublicProfile>,
    isSearching: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSendFriendRequest: (userId: String) -> Unit,
    onAcceptRequest: (requestId: String, fromUserId: String) -> Unit,
    onRejectRequest: (requestId: String) -> Unit,
    onRemoveFriend: (friendId: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundCard,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Friends & Requests",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Search by username...", color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalPurple,
                    unfocusedBorderColor = BorderCard,
                    focusedContainerColor = BackgroundPrimary,
                    unfocusedContainerColor = BackgroundPrimary,
                    cursorColor = RoyalPurple
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Results Section
                if (searchQuery.isNotBlank()) {
                    item {
                        Text(
                            text = "SEARCH RESULTS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = TextMuted
                        )
                    }

                    if (isSearching) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = RoyalPurple, modifier = Modifier.size(24.dp))
                            }
                        }
                    } else if (searchResults.isEmpty()) {
                        item {
                            Text(
                                text = "No users found matching \"$searchQuery\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        items(searchResults, key = { it.userId }) { user ->
                            SearchResultRow(
                                user = user,
                                onAddClick = { onSendFriendRequest(user.userId) },
                                onUserClick = { onUserClick(user.userId) }
                            )
                        }
                    }
                }

                // Pending Requests Section
                if (pendingRequests.isNotEmpty()) {
                    item {
                        Text(
                            text = "PENDING REQUESTS (${pendingRequests.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = TextMuted
                        )
                    }

                    items(pendingRequests, key = { it.id }) { req ->
                        FriendRequestRow(
                            request = req,
                            onAccept = { onAcceptRequest(req.id, req.fromUserId) },
                            onReject = { onRejectRequest(req.id) }
                        )
                    }
                }

                // Friends List Section
                item {
                    Text(
                        text = "YOUR FRIENDS (${friends.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = TextMuted
                    )
                }

                if (friends.isEmpty()) {
                    item {
                        Text(
                            text = "No friends added yet. Search usernames above to build your squad!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(friends, key = { it.userId }) { friend ->
                        FriendRow(
                            friend = friend,
                            onRemove = { onRemoveFriend(friend.userId) },
                            onUserClick = { onUserClick(friend.userId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    user: PublicProfile,
    onAddClick: () -> Unit,
    onUserClick: () -> Unit
) {
    val avatarColor = try {
        Color(android.graphics.Color.parseColor(user.avatarColorHex))
    } catch (_: Exception) {
        RoyalPurple
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundPrimary)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onUserClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
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
                    text = user.displayName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Level ${user.level} · ${user.totalXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = "+ Add",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun FriendRow(
    friend: PublicProfile,
    onRemove: () -> Unit,
    onUserClick: () -> Unit
) {
    val avatarColor = try {
        Color(android.graphics.Color.parseColor(friend.avatarColorHex))
    } catch (_: Exception) {
        RoyalPurple
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundPrimary)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onUserClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
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
                    text = friend.displayName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = friend.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Level ${friend.level} · ${friend.currentMedal.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        OutlinedButton(
            onClick = onRemove,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, VitalityRed.copy(alpha = 0.5f)),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = "Remove",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = VitalityRed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FriendsManagerSheetPreview() {
    QuestRingsTheme {
        FriendsManagerSheet(
            friends = emptyList(),
            pendingRequests = emptyList(),
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false,
            onSearchQueryChanged = {},
            onSendFriendRequest = {},
            onAcceptRequest = { _, _ -> },
            onRejectRequest = {},
            onRemoveFriend = {},
            onUserClick = {},
            onDismiss = {}
        )
    }
}
