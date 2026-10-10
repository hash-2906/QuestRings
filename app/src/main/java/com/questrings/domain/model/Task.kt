package com.questrings.domain.model

data class Task(
    val id: String,
    val userId: String = "",
    val title: String,
    val notes: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.UPCOMING,
    val createdAt: Long = System.currentTimeMillis(),
    val dueAt: Long,
    val completedAt: Long? = null,
    val failedAt: Long? = null,
    val reminders: List<TaskReminder> = emptyList(),
    val xpAwarded: Int = 0
) {
    val isDone: Boolean
        get() = status == TaskStatus.COMPLETED

    fun computeLiveStatus(now: Long = System.currentTimeMillis()): TaskStatus = when {
        status == TaskStatus.COMPLETED -> TaskStatus.COMPLETED
        status == TaskStatus.FAILED    -> TaskStatus.FAILED
        now > dueAt + 86_400_000L     -> TaskStatus.FAILED      // past grace
        now > dueAt                   -> TaskStatus.GRACE_PERIOD
        now > dueAt - 86_400_000L     -> TaskStatus.ACTIVE       // within 24h
        else                          -> TaskStatus.UPCOMING
    }
}
