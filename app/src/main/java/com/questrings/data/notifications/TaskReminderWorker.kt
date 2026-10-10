package com.questrings.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()
        val taskTitle = inputData.getString(KEY_TASK_TITLE) ?: "Quest"
        val badgeLabel = inputData.getString(KEY_BADGE_LABEL) ?: "SIDE QUEST"
        val label = inputData.getString(KEY_LABEL) ?: "soon"
        val colorHex = inputData.getString(KEY_COLOR_HEX) ?: "#0A84FF"

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "quest_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Quest Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled quests"
                enableLights(true)
                lightColor = Color.parseColor(colorHex)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏰ Quest Due Soon — $taskTitle")
            .setContentText("$badgeLabel · Due in $label")
            .setColor(try { Color.parseColor(colorHex) } catch (e: Exception) { Color.BLUE })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId.hashCode(), notification)

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "taskId"
        const val KEY_TASK_TITLE = "taskTitle"
        const val KEY_BADGE_LABEL = "badgeLabel"
        const val KEY_LABEL = "label"
        const val KEY_COLOR_HEX = "colorHex"
    }
}
