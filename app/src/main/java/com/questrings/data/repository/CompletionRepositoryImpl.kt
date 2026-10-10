package com.questrings.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.questrings.data.local.dao.CompletionDao
import com.questrings.data.mapper.toDomain
import com.questrings.data.mapper.toEntity
import com.questrings.domain.model.HabitCompletion
import com.questrings.domain.repository.CompletionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompletionRepositoryImpl @Inject constructor(
    private val completionDao: CompletionDao,
    private val firestore: FirebaseFirestore
) : CompletionRepository {

    override fun observeTodayCompletions(userId: String, dateKey: String): Flow<List<HabitCompletion>> {
        return completionDao.observeTodayCompletions(userId, dateKey).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun markComplete(completion: HabitCompletion): Result<Unit> = runCatching {
        completionDao.insertCompletion(completion.toEntity(isSynced = false))
        try {
            firestore.collection("users")
                .document(completion.userId)
                .collection("completions")
                .document(completion.id)
                .set(completion)
                .await()
            completionDao.markSynced(listOf(completion.id))
        } catch (_: Exception) {
            // Synced via worker
        }
    }

    override suspend fun unmarkComplete(habitId: String, dateKey: String): Result<Unit> = runCatching {
        completionDao.deleteCompletion(habitId, dateKey)
        // Background sync / cleanup handled gracefully
    }

    override suspend fun getCompletionsForRange(
        userId: String,
        from: String,
        to: String
    ): Result<List<HabitCompletion>> = runCatching {
        completionDao.getCompletionsForRange(userId, from, to).map { it.toDomain() }
    }
}
