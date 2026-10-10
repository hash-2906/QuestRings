package com.questrings.domain.config

import com.questrings.domain.model.medal.MedalTier

data class MedalThreshold(
    val tier: MedalTier,
    val requiredXp: Int,
    val requiredLevel: Int,
    val title: String,           // e.g. "Bronze Adventurer"
    val description: String      // e.g. "You've taken your first steps."
)

object MedalProgressionConfig {
    val thresholds: List<MedalThreshold> = listOf(
        MedalThreshold(
            tier = MedalTier.BRONZE,
            requiredXp = 500,
            requiredLevel = 5,
            title = "Bronze Adventurer",
            description = "You've taken your first steps into the quest."
        ),
        MedalThreshold(
            tier = MedalTier.SILVER,
            requiredXp = 2_500,
            requiredLevel = 10,
            title = "Silver Seeker",
            description = "The path grows clearer. Keep climbing."
        ),
        MedalThreshold(
            tier = MedalTier.GOLD,
            requiredXp = 7_500,
            requiredLevel = 20,
            title = "Gold Guardian",
            description = "Few reach this height. You are among them."
        ),
        MedalThreshold(
            tier = MedalTier.PLATINUM,
            requiredXp = 20_000,
            requiredLevel = 35,
            title = "Platinum Sovereign",
            description = "A legend in the making. The summit is near."
        ),
        MedalThreshold(
            tier = MedalTier.OBSIDIAN,
            requiredXp = 50_000,
            requiredLevel = 50,
            title = "Obsidian Overlord",
            description = "You have conquered the quest. Eternal glory is yours."
        )
    )

    /** Returns the highest tier the user qualifies for. */
    fun evaluate(totalXp: Int, level: Int): MedalTier =
        thresholds
            .filter { totalXp >= it.requiredXp && level >= it.requiredLevel }
            .maxByOrNull { it.requiredXp }
            ?.tier ?: MedalTier.NONE

    /** Returns the next threshold the user hasn't yet reached. */
    fun nextThreshold(totalXp: Int, level: Int): MedalThreshold? =
        thresholds.firstOrNull {
            totalXp < it.requiredXp || level < it.requiredLevel
        }
}
