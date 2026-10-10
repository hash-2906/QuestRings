package com.questrings.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class AssignmentGraceExpiredWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val roomId = inputData.getString(KEY_ROOM_ID) ?: return Result.failure()
        val assignmentId = inputData.getString(KEY_ASSIGNMENT_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_ASSIGNMENT_TITLE) ?: "Party Quest"

        runCatching {
            val assignmentRef = firestore.collection("rooms")
                .document(roomId)
                .collection("assignments")
                .document(assignmentId)

            // Mark assignment CLOSED
            assignmentRef.update("status", "CLOSED").await()

            // Auto-reject any pending completions submitted during grace window
            val pendingCompletions = firestore.collection("rooms")
                .document(roomId)
                .collection("completions")
                .whereEqualTo("assignmentId", assignmentId)
                .whereEqualTo("verificationStatus", "PENDING")
                .get()
                .await()

            for (doc in pendingCompletions.documents) {
                doc.reference.update(
                    mapOf(
                        "verificationStatus" to "REJECTED",
                        "rejectionReason" to "Assignment closed before verification was complete."
                    )
                ).await()
            }
        }

        // Send Notification
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Room Assignments",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🏁 Quest Closed — $title")
            .setContentText("Assignment closed. Check out the final leaderboard scores!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((assignmentId + "_closed").hashCode(), notification)

        return Result.success()
    }

    companion object {
        const val KEY_ROOM_ID = "roomId"
        const val KEY_ASSIGNMENT_ID = "assignmentId"
        const val KEY_ASSIGNMENT_TITLE = "assignmentTitle"
        const val CHANNEL_ID = "room_assignments"
    }
}
