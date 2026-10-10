package com.questrings.di

import com.questrings.data.qr.QrCodeGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QrModule {

    @Provides
    @Singleton
    fun provideQrCodeGenerator(): QrCodeGenerator = QrCodeGenerator()
}
