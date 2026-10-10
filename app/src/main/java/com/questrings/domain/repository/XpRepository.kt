package com.questrings.domain.repository

import com.questrings.domain.model.xp.XpEvent
import com.questrings.domain.model.xp.XpSummary
import kotlinx.coroutines.flow.Flow

enum class XpSourceFilter {
    ALL,
    HABITS,
    TASKS,
    ROOMS
}

interface XpRepository {
    fun observeXpSummary(userId: String): Flow<XpSummary>
    fun observeXpHistory(
        userId: String,
        filter: XpSourceFilter = XpSourceFilter.ALL,
        limit: Int = 50
    ): Flow<List<XpEvent>>
    suspend fun recordXpEvent(event: XpEvent): Result<Unit>
    suspend fun getXpEventById(eventId: String): Result<XpEvent?>
}
