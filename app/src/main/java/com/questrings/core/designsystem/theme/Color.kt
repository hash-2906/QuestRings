package com.questrings.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Neutral Palette
val BackgroundPrimary = Color(0xFF000000)
val BackgroundSecondary = Color(0xFF111116)
val BackgroundCard = Color(0xFF17171D)
val BackgroundCardTranslucent = Color(0xAA111116)
val BackgroundTrack = Color(0xFF19191E)
val BackgroundTrackBar = Color(0xFF202025)
val BottomBarBackground = Color(0xFF101014)

val BorderSubtle = Color(0xFF242429)
val BorderCard = Color(0xFF29292F)
val BorderHighlight = Color(0xFF34343A)

val TextPrimary = Color(0xFFF5F5F7)
val TextSecondary = Color(0xFF8E8E98)
val TextMuted = Color(0xFF767680)
val TextLightMuted = Color(0xFF9898A2)
val TextCounter = Color(0xFFD6D6DE)

// Category & Accent Rings
val VitalityRed = Color(0xFFFF2D55)
val IntellectGreen = Color(0xFF30D158)
val DisciplineBlue = Color(0xFF0A84FF)
val StreakOrange = Color(0xFFFF8B35)
val RoyalPurple = Color(0xFF7E5DFF)

// Derived Accents
val StreakGlowBg = Color(0x18FF8B35)
val StreakGlowBorder = Color(0x30FF8B35)

val HeroPink = Color(0xFFFF657E)
val HeroPurple = Color(0xFFA77DFF)

// Gradients
val PrimaryButtonGradient = Brush.horizontalGradient(
    listOf(VitalityRed, RoyalPurple)
)

val XpFillGradient = Brush.horizontalGradient(
    listOf(DisciplineBlue, RoyalPurple)
)

val BrandMarkGradient = Brush.linearGradient(
    listOf(VitalityRed, DisciplineBlue)
)

val HeroTextGradient = Brush.horizontalGradient(
    listOf(HeroPink, HeroPurple)
)

val CardGlossGradient = Brush.verticalGradient(
    listOf(Color(0x15FFFFFF), Color(0x00FFFFFF))
)

@Immutable
data class QuestColors(
    val backgroundPrimary: Color = BackgroundPrimary,
    val backgroundSecondary: Color = BackgroundSecondary,
    val backgroundCard: Color = BackgroundCard,
    val backgroundCardTranslucent: Color = BackgroundCardTranslucent,
    val backgroundTrack: Color = BackgroundTrack,
    val backgroundTrackBar: Color = BackgroundTrackBar,
    val bottomBarBackground: Color = BottomBarBackground,
    val borderSubtle: Color = BorderSubtle,
    val borderCard: Color = BorderCard,
    val borderHighlight: Color = BorderHighlight,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
    val vitalityRed: Color = VitalityRed,
    val intellectGreen: Color = IntellectGreen,
    val disciplineBlue: Color = DisciplineBlue,
    val streakOrange: Color = StreakOrange,
    val royalPurple: Color = RoyalPurple,
    val streakGlowBg: Color = StreakGlowBg,
    val streakGlowBorder: Color = StreakGlowBorder
)

val LocalQuestColors = staticCompositionLocalOf { QuestColors() }
