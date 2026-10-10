package com.questrings.di

import com.questrings.data.repository.LeaderboardRepositoryImpl
import com.questrings.data.repository.ProfileRepositoryImpl
import com.questrings.data.repository.SocialRepositoryImpl
import com.questrings.domain.repository.LeaderboardRepository
import com.questrings.domain.repository.ProfileRepository
import com.questrings.domain.repository.SocialRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocialModule {

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        socialRepositoryImpl: SocialRepositoryImpl
    ): SocialRepository

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(
        leaderboardRepositoryImpl: LeaderboardRepositoryImpl
    ): LeaderboardRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}
