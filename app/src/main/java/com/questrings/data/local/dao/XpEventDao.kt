package com.questrings.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.questrings.data.local.entity.XpEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface XpEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: XpEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<XpEventEntity>)

    @Query("SELECT * FROM xp_events WHERE id = :id")
    suspend fun getEventById(id: String): XpEventEntity?

    @Query("SELECT * FROM xp_events WHERE userId = :userId ORDER BY earnedAt DESC LIMIT :limit")
    fun observeEvents(userId: String, limit: Int = 50): Flow<List<XpEventEntity>>

    @Query("SELECT * FROM xp_events WHERE userId = :userId AND source IN (:sources) ORDER BY earnedAt DESC LIMIT :limit")
    fun observeEventsBySources(userId: String, sources: List<String>, limit: Int = 50): Flow<List<XpEventEntity>>

    @Query("SELECT * FROM xp_events WHERE userId = :userId ORDER BY earnedAt DESC")
    fun observeAllEvents(userId: String): Flow<List<XpEventEntity>>

    @Query("SELECT * FROM xp_events WHERE userId = :userId AND earnedAt >= :sinceTimestamp ORDER BY earnedAt ASC")
    fun observeEventsSince(userId: String, sinceTimestamp: Long): Flow<List<XpEventEntity>>

    @Query("SELECT * FROM xp_events WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedEvents(userId: String): List<XpEventEntity>

    @Query("UPDATE xp_events SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
