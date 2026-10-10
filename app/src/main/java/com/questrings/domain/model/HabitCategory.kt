package com.questrings.domain.model

enum class HabitCategory(val displayName: String, val colorHex: String) {
    VITALITY("Vitality", "#FF2D55"),
    INTELLECT("Intellect", "#30D158"),
    DISCIPLINE("Discipline", "#0A84FF");

    companion object {
        fun fromString(name: String): HabitCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) } ?: VITALITY
        }
    }
}
