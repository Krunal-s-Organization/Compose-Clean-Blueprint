package com.example.composeclean.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.composeclean.data.local.AppDatabase
import com.example.composeclean.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt bindings for the persistence stack: the [AppDatabase] singleton and its DAOs.
 *
 * Register real `Migration` objects here as the schema evolves. `fallbackToDestructive*` is
 * intentionally omitted so a missing migration fails loudly instead of wiping data.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME,
    ).build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
