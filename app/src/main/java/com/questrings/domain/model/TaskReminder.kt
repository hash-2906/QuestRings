package com.questrings.domain.model

data class TaskReminder(
    val id: String,
    val taskId: String,
    val minutesBefore: Int,    // e.g. 60 = 1 hour before, 1440 = 1 day before
    val label: String          // "1 hour before", "1 day before" etc.
)
