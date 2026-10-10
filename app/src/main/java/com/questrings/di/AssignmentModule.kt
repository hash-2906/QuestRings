package com.questrings.di

import com.questrings.data.repository.AssignmentRepositoryImpl
import com.questrings.domain.repository.AssignmentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AssignmentModule {

    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(
        impl: AssignmentRepositoryImpl
    ): AssignmentRepository
}
