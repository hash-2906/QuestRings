package com.questrings.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.questrings.domain.usecase.tasks.AutoFailExpiredTasksUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskGraceExpiredWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val autoFailExpiredTasksUseCase: AutoFailExpiredTasksUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()
        val taskTitle = inputData.getString(KEY_TASK_TITLE) ?: "Quest"

        // Execute auto-fail logic
        autoFailExpiredTasksUseCase()

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "quest_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Quest Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel)
            .setContentTitle("❌ Quest Failed — $taskTitle")
            .setContentText("The grace period has ended. Better luck next time, adventurer.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((taskId + "_fail").hashCode(), notification)

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "taskId"
        const val KEY_TASK_TITLE = "taskTitle"
    }
}
