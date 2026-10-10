package com.questrings.di

import com.questrings.domain.model.xp.AppEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppEventBusModule {

    @Provides
    @Singleton
    fun provideAppEventBus(): MutableSharedFlow<AppEvent> =
        MutableSharedFlow(extraBufferCapacity = 8)
}
