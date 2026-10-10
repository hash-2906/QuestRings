package com.questrings.domain.repository

import com.questrings.domain.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

interface CompletionRepository {
    fun observeTodayCompletions(userId: String, dateKey: String): Flow<List<HabitCompletion>>
    suspend fun markComplete(completion: HabitCompletion): Result<Unit>
    suspend fun unmarkComplete(habitId: String, dateKey: String): Result<Unit>
    suspend fun getCompletionsForRange(
        userId: String, from: String, to: String
    ): Result<List<HabitCompletion>>
}
