package com.example.composeclean.core.data.di

import com.example.composeclean.core.common.IoDispatcher
import com.example.composeclean.core.common.MainDispatcher
import com.example.composeclean.data.repository.UserRepositoryImpl
import com.example.composeclean.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Hilt bindings for the data layer: the [UserRepository] binding and the qualified coroutine
 * dispatchers consumed by [UserRepositoryImpl].
 *
 * Mixes two binding styles, which is why it is an `abstract class`:
 *  - `@Binds` binds the domain-facing interface to its implementation with zero overhead;
 *  - `@Provides` (in the [Companion]) supplies the platform dispatchers, swappable in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    companion object {

        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @MainDispatcher
        fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
    }
}
