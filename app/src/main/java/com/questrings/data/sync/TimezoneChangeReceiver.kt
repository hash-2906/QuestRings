package com.questrings.data.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Listens for system timezone and time adjustments to reschedule MidnightResetWorker
 * accurately against the user's updated local midnight.
 */
class TimezoneChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> {
                MidnightResetWorker.schedule(context)
            }
        }
    }
}
