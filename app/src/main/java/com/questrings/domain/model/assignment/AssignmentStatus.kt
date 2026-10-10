package com.questrings.domain.model.assignment

enum class AssignmentStatus {
    UPCOMING,       // dueAt is in the future (> 24h away)
    ACTIVE,         // within 24h of dueAt
    GRACE,          // past dueAt, within 24h grace window
    CLOSED          // past grace window — no more completions accepted
}
