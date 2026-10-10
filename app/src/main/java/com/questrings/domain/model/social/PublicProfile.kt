package com.questrings.domain.model.social

import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.medal.MedalTier
import com.questrings.domain.model.room.Room

data class PublicProfile(
    val userId: String,
    val displayName: String,
    val avatarColorHex: String,
    val level: Int,
    val currentMedal: MedalTier,
    val currentStreak: Int,
    val totalXp: Int,
    val xpByCategory: Map<HabitCategory, Int> = emptyMap(),
    val recentAchievements: List<SocialFeedEvent> = emptyList(), // Last 3-5 major events
    val mutualRooms: List<Room> = emptyList(), // Computed intersection of rooms
    val friendshipStatus: FriendshipStatus = FriendshipStatus.NONE
)
