package com.questrings.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val avatarColorHex: String,
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
