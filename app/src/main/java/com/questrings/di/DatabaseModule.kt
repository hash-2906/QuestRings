package com.questrings.di

import android.content.Context
import androidx.room.Room
import com.questrings.data.local.QuestRingsDatabase
import com.questrings.data.local.dao.CompletionDao
import com.questrings.data.local.dao.DailyProgressDao
import com.questrings.data.local.dao.HabitDao
import com.questrings.data.local.dao.StatsDao
import com.questrings.data.local.dao.TaskDao
import com.questrings.data.local.dao.XpEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuestRingsDatabase(
        @ApplicationContext context: Context
    ): QuestRingsDatabase {
        return Room.databaseBuilder(
            context,
            QuestRingsDatabase::class.java,
            QuestRingsDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideHabitDao(database: QuestRingsDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideCompletionDao(database: QuestRingsDatabase): CompletionDao = database.completionDao()

    @Provides
    fun provideStatsDao(database: QuestRingsDatabase): StatsDao = database.statsDao()

    @Provides
    fun provideDailyProgressDao(database: QuestRingsDatabase): DailyProgressDao = database.dailyProgressDao()

    @Provides
    fun provideTaskDao(database: QuestRingsDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideXpEventDao(database: QuestRingsDatabase): XpEventDao = database.xpEventDao()
}
