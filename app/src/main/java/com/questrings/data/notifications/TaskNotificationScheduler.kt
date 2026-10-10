package com.questrings.data.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.questrings.domain.model.Task
import com.questrings.domain.model.TaskReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminders(task: Task, reminders: List<TaskReminder>) {
        val now = System.currentTimeMillis()
        for (reminder in reminders) {
            val triggerTime = task.dueAt - (reminder.minutesBefore * 60_000L)
            val delay = triggerTime - now
            if (delay > 0) {
                val data = Data.Builder()
                    .putString(TaskReminderWorker.KEY_TASK_ID, task.id)
                    .putString(TaskReminderWorker.KEY_TASK_TITLE, task.title)
                    .putString(TaskReminderWorker.KEY_BADGE_LABEL, task.priority.badgeLabel)
                    .putString(TaskReminderWorker.KEY_LABEL, reminder.label)
                    .putString(TaskReminderWorker.KEY_COLOR_HEX, task.priority.colorHex)
                    .build()

                val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("task_${task.id}")
                    .addTag("reminder_${reminder.id}")
                    .build()

                workManager.enqueueUniqueWork(
                    "reminder_${reminder.id}",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }

    fun scheduleDueNowNotification(task: Task) {
        val now = System.currentTimeMillis()
        val delay = task.dueAt - now
        if (delay > 0) {
            val data = Data.Builder()
                .putString(TaskDueNowWorker.KEY_TASK_ID, task.id)
                .putString(TaskDueNowWorker.KEY_TASK_TITLE, task.title)
                .putString(TaskDueNowWorker.KEY_BADGE_LABEL, task.priority.badgeLabel)
                .putString(TaskDueNowWorker.KEY_COLOR_HEX, task.priority.colorHex)
                .build()

            val request = OneTimeWorkRequestBuilder<TaskDueNowWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("task_${task.id}")
                .build()

            workManager.enqueueUniqueWork(
                "due_${task.id}",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    fun scheduleGraceExpiry(task: Task) {
        val now = System.currentTimeMillis()
        val graceExpiryTime = task.dueAt + 86_400_000L // 24 hours later
        val delay = graceExpiryTime - now
        if (delay > 0) {
            val data = Data.Builder()
                .putString(TaskGraceExpiredWorker.KEY_TASK_ID, task.id)
                .putString(TaskGraceExpiredWorker.KEY_TASK_TITLE, task.title)
                .build()

            val request = OneTimeWorkRequestBuilder<TaskGraceExpiredWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("task_${task.id}")
                .build()

            workManager.enqueueUniqueWork(
                "grace_${task.id}",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    fun cancelAllForTask(taskId: String) {
        workManager.cancelAllWorkByTag("task_$taskId")
    }
}
