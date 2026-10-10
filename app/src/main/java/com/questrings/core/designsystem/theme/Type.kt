package com.questrings.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.questrings.R

val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val DmSansName = GoogleFont("DM Sans")
val DmMonoName = GoogleFont("DM Mono")
val PlayfairDisplayName = GoogleFont("Playfair Display")

val DmSansFontFamily = FontFamily(
    Font(googleFont = DmSansName, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = DmSansName, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = DmSansName, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = DmSansName, fontProvider = fontProvider, weight = FontWeight.Bold)
)

val DmMonoFontFamily = FontFamily(
    Font(googleFont = DmMonoName, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = DmMonoName, fontProvider = fontProvider, weight = FontWeight.Medium)
)

val PlayfairDisplayFontFamily = FontFamily(
    Font(googleFont = PlayfairDisplayName, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlayfairDisplayName, fontProvider = fontProvider, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(googleFont = PlayfairDisplayName, fontProvider = fontProvider, weight = FontWeight.Bold, style = FontStyle.Italic)
)

@Immutable
data class QuestTypography(
    // Hero Display
    val heroTitle: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-1.5).sp,
        color = TextPrimary
    ),
    val heroAccent: TextStyle = TextStyle(
        fontFamily = PlayfairDisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        fontSize = 42.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.5).sp
    ),
    // Headings
    val headlineLarge: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.8).sp,
        color = TextPrimary
    ),
    val headlineMedium: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.6).sp,
        color = TextPrimary
    ),
    val titleMedium: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.3).sp,
        color = TextPrimary
    ),
    val titleSmall: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextPrimary
    ),
    // Body
    val bodyLarge: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = TextSecondary
    ),
    val bodyMedium: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = TextSecondary
    ),
    val bodySmall: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        color = TextMuted
    ),
    // Monospace / Badges / Counters / Kicker
    val monoKicker: TextStyle = TextStyle(
        fontFamily = DmMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp,
        color = TextMuted
    ),
    val monoCounter: TextStyle = TextStyle(
        fontFamily = DmMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TextCounter
    ),
    val ringCenterPercent: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 42.sp,
        letterSpacing = (-1.5).sp,
        color = TextPrimary
    ),
    val ringCenterKicker: TextStyle = TextStyle(
        fontFamily = DmMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 1.2.sp,
        color = TextSecondary
    ),
    // Button
    val button: TextStyle = TextStyle(
        fontFamily = DmSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = TextPrimary
    )
)

val LocalQuestTypography = staticCompositionLocalOf { QuestTypography() }
