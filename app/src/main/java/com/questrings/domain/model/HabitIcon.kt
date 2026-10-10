package com.questrings.domain.model

enum class HabitIcon {
    DROPLETS,
    DUMBBELL,
    BOOK_OPEN,
    BRAIN,
    SPARKLES,
    MOON,
    TIMER,
    TARGET,
    FLAME,
    HEART,
    STAR,
    SHIELD;

    companion object {
        fun fromString(name: String): HabitIcon {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: TARGET
        }
    }
}
