package com.questrings.domain.model.social

data class SocialFeedEvent(
    val id: String,
    val userId: String,
    val displayName: String,
    val avatarColorHex: String,
    val eventType: FeedEventType,
    val description: String,      // e.g., "unlocked Gold Medal", "closed all 3 rings"
    val timestamp: Long,
    val xpEarned: Int,
    val reactions: Map<String, List<String>> = emptyMap() // Map<Emoji, List<UserId>>
)
