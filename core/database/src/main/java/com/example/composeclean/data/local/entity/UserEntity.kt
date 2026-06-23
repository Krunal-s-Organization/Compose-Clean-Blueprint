package com.example.composeclean.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached user row in the local database.
 *
 * Column names are pinned with [ColumnInfo] so that refactoring the Kotlin property names never
 * silently changes the database schema (which would otherwise require a migration).
 */
@Entity(tableName = UserEntity.TABLE_NAME)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    /** Epoch millis of the last successful sync, useful for cache-invalidation policies. */
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}
