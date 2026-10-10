package com.questrings.domain.usecase.tasks

import com.questrings.data.local.dao.TaskDao
import javax.inject.Inject

class AutoFailExpiredTasksUseCase @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend operator fun invoke(): Result<Int> = runCatching {
        val now = System.currentTimeMillis()
        val expired = taskDao.getExpiredTasksPastGrace(now)
        for (task in expired) {
            taskDao.updateFailed(task.id, now)
        }
        expired.size
    }
}
