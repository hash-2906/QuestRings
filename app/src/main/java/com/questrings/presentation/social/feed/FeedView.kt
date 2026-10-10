package com.questrings.presentation.social.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.questrings.core.designsystem.theme.QuestRingsTheme
import com.questrings.core.designsystem.theme.TextMuted
import com.questrings.core.designsystem.theme.TextSecondary
import com.questrings.domain.model.social.FeedEventType
import com.questrings.domain.model.social.SocialFeedEvent

@Composable
fun FeedView(
    feedEvents: List<SocialFeedEvent>,
    currentUserId: String,
    onUserClick: (userId: String) -> Unit,
    onToggleReaction: (eventId: String, emoji: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (feedEvents.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            com.questrings.core.designsystem.components.QuestEmptyState(
                iconEmoji = "📡",
                title = "Circle Feed Inactive",
                description = "Connect with fellow adventurers, unlock milestone medals, or complete room quests to broadcast achievements."
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(feedEvents, key = { it.id }) { event ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                ) {
                    SocialFeedCard(
                        event = event,
                        currentUserId = currentUserId,
                        onUserClick = onUserClick,
                        onToggleReaction = { emoji -> onToggleReaction(event.id, emoji) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun FeedViewPreview() {
    QuestRingsTheme {
        FeedView(
            feedEvents = listOf(
                SocialFeedEvent(
                    id = "1",
                    userId = "u1",
                    displayName = "Riya",
                    avatarColorHex = "#FF2D55",
                    eventType = FeedEventType.MEDAL_UNLOCKED,
                    description = "unlocked Gold Medal 🏆",
                    timestamp = System.currentTimeMillis() - 7200000,
                    xpEarned = 7500,
                    reactions = mapOf("👏" to listOf("u2"), "🔥" to listOf("me"))
                )
            ),
            currentUserId = "me",
            onUserClick = {},
            onToggleReaction = { _, _ -> }
        )
    }
}
