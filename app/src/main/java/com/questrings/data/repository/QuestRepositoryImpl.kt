package com.questrings.data.repository

import com.questrings.data.datasource.LocalQuestDataSource
import com.questrings.domain.model.Habit
import com.questrings.domain.model.Task
import com.questrings.domain.model.UserProfile
import com.questrings.domain.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepositoryImpl @Inject constructor(
    private val localDataSource: LocalQuestDataSource
) : QuestRepository {

    override fun getUserProfile(): Flow<UserProfile> = localDataSource.userProfile

    override fun getHabits(): Flow<List<Habit>> = localDataSource.habits

    override fun getTasks(): Flow<List<Task>> = localDataSource.tasks

    override suspend fun toggleHabit(id: String) {
        localDataSource.toggleHabit(id)
    }

    override suspend fun addHabit(habit: Habit) {
        localDataSource.addHabit(habit)
    }

    override suspend fun resetDay() {
        localDataSource.resetDay()
    }

    override suspend fun toggleTask(id: String) {
        localDataSource.toggleTask(id)
    }

    override suspend fun addTask(task: Task) {
        localDataSource.addTask(task)
    }

    override suspend fun deleteTask(id: String) {
        localDataSource.deleteTask(id)
    }
}
