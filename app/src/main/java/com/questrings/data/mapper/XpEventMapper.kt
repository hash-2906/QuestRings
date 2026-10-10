package com.questrings.data.mapper

import com.questrings.data.local.entity.XpEventEntity
import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSource

fun XpEventEntity.toDomain(): XpEvent {
    return XpEvent(
        id = id,
        userId = userId,
        source = runCatching { XpSource.valueOf(source) }.getOrDefault(XpSource.HABIT_COMPLETION),
        baseXp = baseXp,
        bonusXp = bonusXp,
        totalXp = totalXp,
        multiplier = multiplier,
        description = description,
        sourceEntityId = sourceEntityId,
        sourceEntityTitle = sourceEntityTitle,
        earnedAt = earnedAt,
        cumulativeXpAfter = cumulativeXpAfter
    )
}

fun XpEvent.toEntity(isSynced: Boolean = false): XpEventEntity {
    return XpEventEntity(
        id = id,
        userId = userId,
        source = source.name,
        baseXp = baseXp,
        bonusXp = bonusXp,
        totalXp = totalXp,
        multiplier = multiplier,
        description = description,
        sourceEntityId = sourceEntityId,
        sourceEntityTitle = sourceEntityTitle,
        earnedAt = earnedAt,
        cumulativeXpAfter = cumulativeXpAfter,
        isSynced = isSynced
    )
}

fun XpEvent.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "userId" to userId,
        "source" to source.name,
        "baseXp" to baseXp,
        "bonusXp" to bonusXp,
        "totalXp" to totalXp,
        "multiplier" to multiplier.toDouble(),
        "description" to description,
        "sourceEntityId" to sourceEntityId,
        "sourceEntityTitle" to sourceEntityTitle,
        "earnedAt" to earnedAt,
        "cumulativeXpAfter" to cumulativeXpAfter
    )
}

fun Map<String, Any?>.toXpEvent(): XpEvent? {
    val id = this["id"] as? String ?: return null
    val userId = this["userId"] as? String ?: return null
    val sourceStr = this["source"] as? String ?: return null
    val source = runCatching { XpSource.valueOf(sourceStr) }.getOrDefault(XpSource.HABIT_COMPLETION)
    val baseXp = (this["baseXp"] as? Number)?.toInt() ?: 0
    val bonusXp = (this["bonusXp"] as? Number)?.toInt() ?: 0
    val totalXp = (this["totalXp"] as? Number)?.toInt() ?: (baseXp + bonusXp)
    val multiplier = (this["multiplier"] as? Number)?.toFloat() ?: 1.0f
    val description = this["description"] as? String ?: ""
    val sourceEntityId = this["sourceEntityId"] as? String
    val sourceEntityTitle = this["sourceEntityTitle"] as? String
    val earnedAt = (this["earnedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
    val cumulativeXpAfter = (this["cumulativeXpAfter"] as? Number)?.toInt() ?: 0

    return XpEvent(
        id = id,
        userId = userId,
        source = source,
        baseXp = baseXp,
        bonusXp = bonusXp,
        totalXp = totalXp,
        multiplier = multiplier,
        description = description,
        sourceEntityId = sourceEntityId,
        sourceEntityTitle = sourceEntityTitle,
        earnedAt = earnedAt,
        cumulativeXpAfter = cumulativeXpAfter
    )
}
