package com.questrings.domain.repository

import com.questrings.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabitsForUser(userId: String): Flow<List<Habit>>
    suspend fun addHabit(habit: Habit): Result<Unit>
    suspend fun updateHabit(habit: Habit): Result<Unit>
    suspend fun deleteHabit(habitId: String): Result<Unit>
    suspend fun seedDefaultHabits(userId: String): Result<Unit>
}
