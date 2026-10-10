package com.questrings.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.questrings.data.local.dao.CompletionDao
import com.questrings.data.local.dao.DailyProgressDao
import com.questrings.data.local.dao.HabitDao
import com.questrings.data.local.dao.StatsDao
import com.questrings.data.local.dao.TaskDao
import com.questrings.data.local.dao.XpEventDao
import com.questrings.data.local.entity.DailyProgressEntity
import com.questrings.data.local.entity.HabitCompletionEntity
import com.questrings.data.local.entity.HabitEntity
import com.questrings.data.local.entity.TaskEntity
import com.questrings.data.local.entity.TaskReminderEntity
import com.questrings.data.local.entity.UserStatsEntity
import com.questrings.data.local.entity.XpEventEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        UserStatsEntity::class,
        DailyProgressEntity::class,
        TaskEntity::class,
        TaskReminderEntity::class,
        XpEventEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class QuestRingsDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao
    abstract fun statsDao(): StatsDao
    abstract fun dailyProgressDao(): DailyProgressDao
    abstract fun taskDao(): TaskDao
    abstract fun xpEventDao(): XpEventDao

    companion object {
        const val DATABASE_NAME = "questrings_db"
    }
}
