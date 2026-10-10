package com.questrings.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class QuestShapes(
    val small: Shape = RoundedCornerShape(8.dp),
    val medium: Shape = RoundedCornerShape(12.dp),
    val large: Shape = RoundedCornerShape(18.dp),
    val extraLarge: Shape = RoundedCornerShape(24.dp),
    val pill: Shape = RoundedCornerShape(999.dp),
    val circle: Shape = CircleShape
)

val LocalQuestShapes = staticCompositionLocalOf { QuestShapes() }
