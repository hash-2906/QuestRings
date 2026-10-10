package com.questrings.domain.model.medal

enum class MedalTier(
    val displayName: String,
    val emoji: String,
    val colorHex: String,
    val glowColorHex: String
) {
    NONE("None", "", "#444444", "#444444"),
    BRONZE("Bronze", "🥉", "#CD7F32", "#CD7F3260"),
    SILVER("Silver", "🥈", "#A8A9AD", "#A8A9AD60"),
    GOLD("Gold", "🥇", "#FFD700", "#FFD70060"),
    PLATINUM("Platinum", "💎", "#E5E4E2", "#B0C4DE60"),
    OBSIDIAN("Obsidian", "⬛", "#1C1C1E", "#7E5DFF60")
}
