package com.questrings.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.questrings.data.local.dao.StatsDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class MidnightResetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val statsDao: StatsDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val cal = Calendar.getInstance()
        val isMonday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
        val isFirstOfMonth = cal.get(Calendar.DAY_OF_MONTH) == 1

        if (isMonday) {
            statsDao.resetWeeklyXp()
        }
        if (isFirstOfMonth) {
            statsDao.resetMonthlyXp()
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "MidnightResetWorker"

        fun schedule(context: Context) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_MONTH, 1)
            }
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val dailyWorkRequest = PeriodicWorkRequestBuilder<MidnightResetWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyWorkRequest
            )
        }
    }
}
