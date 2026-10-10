package com.questrings.core.haptics

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Tactile feedback engine for QuestRings.
 * Supports light impact, heavy success patterns, and error haptic pulses.
 */
class QuestHapticFeedback(
    private val composeHaptics: HapticFeedback,
    private val context: Context
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Light tactile impact:
     * Used for tapping bottom navigation tabs, checking checklist items,
     * clicking emoji reactions, and toggling filters.
     */
    fun lightImpact() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && vibrator?.hasVibrator() == true) {
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                vibrator?.vibrate(effect)
            } else {
                composeHaptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        } catch (_: Exception) {
            composeHaptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    /**
     * Heavy / Success tactile impact:
     * Used when closing a habit ring, verifying a room assignment,
     * leveling up, or claiming an unlocked medal tier.
     */
    fun heavySuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
                // Two pulse burst pattern: short prompt, then triumphant sustained buzz
                val timings = longArrayOf(0, 45, 60, 110)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator?.vibrate(effect)
            } else {
                composeHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        } catch (_: Exception) {
            composeHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    /**
     * Error tactile impact:
     * Used for entering invalid room join codes, incorrect authentication credentials,
     * or failing to submit required checklist items.
     */
    fun errorImpact() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
                // Rapid stutter pulse
                val timings = longArrayOf(0, 30, 40, 30, 40, 50)
                val amplitudes = intArrayOf(0, 200, 0, 200, 0, 240)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator?.vibrate(effect)
            } else {
                composeHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        } catch (_: Exception) {
            composeHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}

/**
 * Remember helper to inject QuestHapticFeedback into any Composable.
 */
@Composable
fun rememberQuestHaptics(): QuestHapticFeedback {
    val composeHaptics = LocalHapticFeedback.current
    val context = LocalContext.current.applicationContext
    return remember(composeHaptics, context) {
        QuestHapticFeedback(composeHaptics, context)
    }
}
