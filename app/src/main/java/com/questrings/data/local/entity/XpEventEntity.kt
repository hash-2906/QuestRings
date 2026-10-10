package com.questrings.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "xp_events",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["earnedAt"]),
        Index(value = ["source"])
    ]
)
data class XpEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val source: String,           // XpSource.name
    val baseXp: Int,
    val bonusXp: Int,
    val totalXp: Int,
    val multiplier: Float,
    val description: String,
    val sourceEntityId: String?,
    val sourceEntityTitle: String?,
    val earnedAt: Long,
    val cumulativeXpAfter: Int,
    val isSynced: Boolean = false
)
