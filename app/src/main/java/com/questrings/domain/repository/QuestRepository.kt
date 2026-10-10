package com.questrings.domain.repository

import com.questrings.domain.model.Habit
import com.questrings.domain.model.Task
import com.questrings.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface QuestRepository {
    fun getUserProfile(): Flow<UserProfile>
    fun getHabits(): Flow<List<Habit>>
    fun getTasks(): Flow<List<Task>>
    suspend fun toggleHabit(id: String)
    suspend fun addHabit(habit: Habit)
    suspend fun resetDay()
    suspend fun toggleTask(id: String)
    suspend fun addTask(task: Task)
    suspend fun deleteTask(id: String)
}
