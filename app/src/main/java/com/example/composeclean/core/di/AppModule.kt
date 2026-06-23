package com.example.composeclean.core.di

import android.content.Context
import androidx.room.Room
import com.example.composeclean.core.network.AuthTokenProvider
import com.example.composeclean.data.local.AppDatabase
import com.example.composeclean.data.local.dao.UserDao
import com.example.composeclean.data.repository.UserRepositoryImpl
import com.example.composeclean.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Application-wide Hilt bindings installed in the [SingletonComponent].
 *
 * The module mixes two binding styles, which is why it is an `abstract class`:
 *  - `@Binds` abstract functions bind an interface to its implementation with zero overhead
 *    (used here for [UserRepository] → [UserRepositoryImpl]);
 *  - `@Provides` functions inside the [Companion] object construct concrete instances Hilt cannot
 *    create itself (Room, DataStore, dispatchers, third-party types).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /** Binds the repository interface used by the domain layer to its data-layer implementation. */
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            // Register real Migration objects here as the schema evolves. fallbackToDestructive*
            // is intentionally omitted so a missing migration fails loudly instead of wiping data.
            .build()

        @Provides
        fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

        @Provides
        @IoDispatcher
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @MainDispatcher
        fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        /**
         * Supplies the auth token to the network layer. Replace this stub with a real
         * implementation that reads the persisted session token (e.g. from DataStore).
         */
        @Provides
        @Singleton
        fun provideAuthTokenProvider(): AuthTokenProvider = AuthTokenProvider { null }
    }
}
