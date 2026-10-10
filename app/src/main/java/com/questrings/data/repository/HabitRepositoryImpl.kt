package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.questrings.data.local.dao.HabitDao
import com.questrings.data.mapper.toDomain
import com.questrings.data.mapper.toEntity
import com.questrings.domain.model.Habit
import com.questrings.domain.model.HabitCategory
import com.questrings.domain.model.HabitIcon
import com.questrings.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val firestore: FirebaseFirestore
) : HabitRepository {

    override fun observeHabitsForUser(userId: String): Flow<List<Habit>> {
        return habitDao.observeHabitsForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addHabit(habit: Habit): Result<Unit> = runCatching {
        // Local first
        habitDao.insertHabit(habit.toEntity(isSynced = false))
        try {
            firestore.collection("users")
                .document(habit.userId)
                .collection("habits")
                .document(habit.id)
                .set(habit)
                .await()
            habitDao.markSynced(listOf(habit.id))
        } catch (_: Exception) {
            // Unsynced will be retried by HabitSyncWorker
        }
    }

    override suspend fun updateHabit(habit: Habit): Result<Unit> = runCatching {
        habitDao.updateHabit(habit.toEntity(isSynced = false))
        try {
            firestore.collection("users")
                .document(habit.userId)
                .collection("habits")
                .document(habit.id)
                .set(habit)
                .await()
            habitDao.markSynced(listOf(habit.id))
        } catch (_: Exception) {
            // Sync later
        }
    }

    override suspend fun deleteHabit(habitId: String): Result<Unit> = runCatching {
        val habit = habitDao.getHabitById(habitId)
        habitDao.deleteHabit(habitId)
        if (habit != null) {
            try {
                firestore.collection("users")
                    .document(habit.userId)
                    .collection("habits")
                    .document(habitId)
                    .delete()
                    .await()
            } catch (_: Exception) {
                // Ignore failure
            }
        }
    }

    override suspend fun seedDefaultHabits(userId: String): Result<Unit> = runCatching {
        val count = habitDao.getHabitCount(userId)
        if (count == 0) {
            val now = System.currentTimeMillis()
            val defaultHabits = listOf(
                Habit(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Hydration Quest",
                    goal = "Drink 2.5L water",
                    category = HabitCategory.VITALITY,
                    icon = HabitIcon.DROPLETS,
                    isDefault = true,
                    createdAt = now,
                    sortOrder = 0
                ),
                Habit(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Deep Focus Tome",
                    goal = "Read 20 pages or learn",
                    category = HabitCategory.INTELLECT,
                    icon = HabitIcon.BOOK_OPEN,
                    isDefault = true,
                    createdAt = now,
                    sortOrder = 1
                ),
                Habit(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Iron Discipline",
                    goal = "30 mins workout / movement",
                    category = HabitCategory.DISCIPLINE,
                    icon = HabitIcon.DUMBBELL,
                    isDefault = true,
                    createdAt = now,
                    sortOrder = 2
                )
            )
            defaultHabits.forEach { addHabit(it) }
        }
    }
}
