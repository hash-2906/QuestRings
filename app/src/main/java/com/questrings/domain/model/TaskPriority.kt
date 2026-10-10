package com.questrings.domain.model

enum class TaskPriority(
    val displayName: String,
    val colorHex: String,
    val xpReward: Int,
    val badgeLabel: String
) {
    LOW("Low", "#30D158", 30, "EASY QUEST"),
    MEDIUM("Medium", "#0A84FF", 60, "SIDE QUEST"),
    HIGH("High", "#FF8B35", 100, "MAIN QUEST"),
    URGENT("Urgent", "#FF2D55", 150, "BOSS QUEST")
}
