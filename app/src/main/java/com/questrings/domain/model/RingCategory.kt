package com.questrings.domain.model

import androidx.compose.ui.graphics.Color
import com.questrings.core.designsystem.theme.DisciplineBlue
import com.questrings.core.designsystem.theme.IntellectGreen
import com.questrings.core.designsystem.theme.VitalityRed

enum class RingCategory(
    val title: String,
    val color: Color
) {
    Vitality("Vitality", VitalityRed),
    Intellect("Intellect", IntellectGreen),
    Discipline("Discipline", DisciplineBlue);

    companion object {
        fun fromString(name: String): RingCategory {
            return entries.firstOrNull { it.title.equals(name, ignoreCase = true) } ?: Vitality
        }
    }
}
