package com.questrings.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.questrings.domain.model.assignment.Assignment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Room Quests & Leaderboards",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for room quest assignments, due reminders, and leaderboard updates."
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleAssignmentGraceWorker(assignment: Assignment) {
        val now = System.currentTimeMillis()
        val graceEndTime = assignment.dueAt + (24 * 60 * 60 * 1000L)
        val delayMs = (graceEndTime - now).coerceAtLeast(0L)

        val inputData = Data.Builder()
            .putString(AssignmentGraceExpiredWorker.KEY_ROOM_ID, assignment.roomId)
            .putString(AssignmentGraceExpiredWorker.KEY_ASSIGNMENT_ID, assignment.id)
            .putString(AssignmentGraceExpiredWorker.KEY_ASSIGNMENT_TITLE, assignment.title)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AssignmentGraceExpiredWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "assignment_grace_${assignment.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun sendAssignmentPostedNotification(roomName: String, title: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📋 New Quest Posted — $roomName")
            .setContentText("$title · A new party challenge is ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((roomName + title + System.currentTimeMillis()).hashCode(), notification)
    }

    fun sendVerificationApprovedNotification(assignmentTitle: String, rank: Int, points: Int, xp: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle("✅ Quest Approved — Rank #$rank!")
            .setContentText("$assignmentTitle · +$points pts · +$xp XP awarded to your rank!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((assignmentTitle + "_approved").hashCode(), notification)
    }

    fun sendVerificationRejectedNotification(assignmentTitle: String, reason: String) {
        val displayReason = reason.ifBlank { "Contact your party leader for feedback." }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentTitle("❌ Completion Not Approved")
            .setContentText("$assignmentTitle: $displayReason")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((assignmentTitle + "_rejected").hashCode(), notification)
    }

    fun cancelAssignmentWork(assignmentId: String) {
        workManager.cancelUniqueWork("assignment_grace_$assignmentId")
    }

    companion object {
        const val CHANNEL_ID = "room_assignments"
    }
}
