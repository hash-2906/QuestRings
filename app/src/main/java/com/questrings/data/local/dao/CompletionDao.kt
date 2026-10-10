package com.questrings.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.questrings.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionDao {
    @Query("SELECT * FROM habit_completions WHERE userId = :userId AND dateKey = :dateKey")
    fun observeTodayCompletions(userId: String, dateKey: String): Flow<List<HabitCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletionEntity)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateKey = :dateKey")
    suspend fun deleteCompletion(habitId: String, dateKey: String)

    @Query("SELECT * FROM habit_completions WHERE userId = :userId AND dateKey BETWEEN :from AND :to ORDER BY completedAt ASC")
    suspend fun getCompletionsForRange(userId: String, from: String, to: String): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completions WHERE isSynced = 0")
    suspend fun getUnsyncedCompletions(): List<HabitCompletionEntity>

    @Query("UPDATE habit_completions SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
