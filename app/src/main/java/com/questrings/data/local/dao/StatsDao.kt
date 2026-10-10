package com.questrings.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.questrings.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun observeUserStats(userId: String): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    suspend fun getUserStats(userId: String): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE isSynced = 0")
    suspend fun getUnsyncedStats(): List<UserStatsEntity>

    @Query("UPDATE user_stats SET isSynced = 1 WHERE userId = :userId")
    suspend fun markSynced(userId: String)

    @Query("UPDATE user_stats SET xpWeekly = 0")
    suspend fun resetWeeklyXp()

    @Query("UPDATE user_stats SET xpMonthly = 0")
    suspend fun resetMonthlyXp()
}
