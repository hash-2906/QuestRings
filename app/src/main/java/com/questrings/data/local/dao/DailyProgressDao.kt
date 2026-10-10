package com.questrings.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.questrings.data.local.entity.DailyProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyProgressDao {
    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND dateKey = :dateKey LIMIT 1")
    fun observeDailyProgress(userId: String, dateKey: String): Flow<DailyProgressEntity?>

    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND dateKey BETWEEN :from AND :to ORDER BY dateKey ASC")
    suspend fun getDailyProgressHistory(userId: String, from: String, to: String): List<DailyProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyProgress(progress: DailyProgressEntity)

    @Query("SELECT * FROM daily_progress WHERE isSynced = 0")
    suspend fun getUnsyncedDailyProgress(): List<DailyProgressEntity>

    @Query("UPDATE daily_progress SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
