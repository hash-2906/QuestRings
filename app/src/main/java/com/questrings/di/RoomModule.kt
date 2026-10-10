package com.questrings.di

import com.questrings.data.repository.RoomRepositoryImpl
import com.questrings.domain.repository.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomModule {

    @Binds
    @Singleton
    abstract fun bindRoomRepository(
        roomRepositoryImpl: RoomRepositoryImpl
    ): RoomRepository
}
