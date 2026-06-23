package com.example.composeclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composeclean.data.local.dao.UserDao
import com.example.composeclean.data.local.entity.UserEntity

/**
 * The application's Room database.
 *
 * ### Versioning & migrations
 * Bump [DATABASE_VERSION] whenever the schema changes and register a corresponding
 * `androidx.room.migration.Migration` in `DatabaseModule`. Exporting schemas (see the `room`
 * KSP arg in `app/build.gradle.kts`) lets you diff schema JSON across versions and write safe,
 * tested migrations rather than relying on destructive fallbacks in production.
 */
@Database(
    entities = [UserEntity::class],
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "compose_clean.db"
        const val DATABASE_VERSION = 1
    }
}
