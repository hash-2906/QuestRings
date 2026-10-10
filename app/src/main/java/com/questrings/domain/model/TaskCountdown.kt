package com.questrings.domain.model

data class TaskCountdown(
    val totalMs: Long,
    val remainingMs: Long,
    val progressFraction: Float,   // 0f–1f for the progress bar
    val displayLabel: String,      // "2d 4h" / "01:23:45" / "Grace: 12:00:00"
    val isCountingDown: Boolean,   // true when within 24h
    val isInGrace: Boolean
)
