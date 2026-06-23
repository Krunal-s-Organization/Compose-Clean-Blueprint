package com.example.composeclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composeclean.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data-access object for the [UserEntity] table.
 *
 * Read queries return [Flow] so the database can act as the single source of truth: any write
 * (from a network sync) automatically re-emits to every active observer. Write operations are
 * `suspend` functions and must be called from a coroutine.
 */
@Dao
interface UserDao {

    /** Observes all cached users, ordered by name. Emits a new list on every table change. */
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun observeUsers(): Flow<List<UserEntity>>

    /** Observes a single cached user by [id]; emits `null` when the row is absent. */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUserById(id: Int): Flow<UserEntity?>

    /** Inserts or replaces a batch of users (used when syncing the full list from the network). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<UserEntity>)

    /** Inserts or replaces a single user. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    /** Clears the cache, e.g. on logout or a forced full refresh. */
    @Query("DELETE FROM users")
    suspend fun clear()
}
