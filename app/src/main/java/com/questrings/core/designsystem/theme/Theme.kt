package com.questrings.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val DarkColorScheme = darkColorScheme(
    primary = VitalityRed,
    secondary = DisciplineBlue,
    tertiary = RoyalPurple,
    background = BackgroundPrimary,
    surface = BackgroundSecondary,
    surfaceVariant = BackgroundCard,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderCard,
    outlineVariant = BorderSubtle
)

@Composable
fun QuestRingsTheme(
    content: @Composable () -> Unit
) {
    val questColors = QuestColors()
    val questTypography = QuestTypography()
    val questShapes = QuestShapes()

    CompositionLocalProvider(
        LocalQuestColors provides questColors,
        LocalQuestTypography provides questTypography,
        LocalQuestShapes provides questShapes
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}

object QuestTheme {
    val colors: QuestColors
        @Composable
        @ReadOnlyComposable
        get() = LocalQuestColors.current

    val typography: QuestTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalQuestTypography.current

    val shapes: QuestShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalQuestShapes.current
}
