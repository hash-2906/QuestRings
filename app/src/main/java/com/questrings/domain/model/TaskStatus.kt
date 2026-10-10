package com.questrings.domain.model

enum class TaskStatus {
    UPCOMING,      // due date in the future (> 24h)
    ACTIVE,        // within 24h of due time
    GRACE_PERIOD,  // past due, within 24h grace window
    COMPLETED,
    FAILED         // past grace period, incomplete
}
