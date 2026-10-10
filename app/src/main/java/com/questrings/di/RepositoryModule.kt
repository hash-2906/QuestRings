package com.questrings.di

import com.questrings.data.repository.CompletionRepositoryImpl
import com.questrings.data.repository.HabitRepositoryImpl
import com.questrings.data.repository.MedalRepositoryImpl
import com.questrings.data.repository.QuestRepositoryImpl
import com.questrings.data.repository.StatsRepositoryImpl
import com.questrings.data.repository.TaskRepositoryImpl
import com.questrings.data.repository.XpRepositoryImpl
import com.questrings.domain.repository.CompletionRepository
import com.questrings.domain.repository.HabitRepository
import com.questrings.domain.repository.MedalRepository
import com.questrings.domain.repository.QuestRepository
import com.questrings.domain.repository.StatsRepository
import com.questrings.domain.repository.TaskRepository
import com.questrings.domain.repository.XpRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuestRepository(
        questRepositoryImpl: QuestRepositoryImpl
    ): QuestRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        habitRepositoryImpl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindCompletionRepository(
        completionRepositoryImpl: CompletionRepositoryImpl
    ): CompletionRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        statsRepositoryImpl: StatsRepositoryImpl
    ): StatsRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindXpRepository(
        xpRepositoryImpl: XpRepositoryImpl
    ): XpRepository

    @Binds
    @Singleton
    abstract fun bindMedalRepository(
        medalRepositoryImpl: MedalRepositoryImpl
    ): MedalRepository
}
