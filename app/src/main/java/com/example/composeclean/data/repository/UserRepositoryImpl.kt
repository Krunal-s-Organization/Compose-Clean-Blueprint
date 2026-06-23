package com.example.composeclean.data.repository

import com.example.composeclean.core.common.Resource
import com.example.composeclean.core.di.IoDispatcher
import com.example.composeclean.data.local.dao.UserDao
import com.example.composeclean.data.mapper.toDomain
import com.example.composeclean.data.mapper.toDomainList
import com.example.composeclean.data.mapper.toEntities
import com.example.composeclean.data.mapper.toEntity
import com.example.composeclean.data.remote.api.UserApi
import com.example.composeclean.domain.model.User
import com.example.composeclean.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of [UserRepository].
 *
 * ### Strategy
 * The local Room database is the **single source of truth**. Every read:
 *  1. emits the cached data immediately (wrapped in [Resource.Loading]) so the UI never shows a
 *     blank screen when data is already on disk;
 *  2. attempts to refresh from the network and writes the result back into the database;
 *  3. observes the database, so any write — now or later — re-emits the latest data to collectors.
 *
 * Network failures are caught and converted into [Resource.Error] that still carries the cached
 * data, so the user keeps seeing content while being informed the refresh failed.
 *
 * The whole pipeline runs on the injected [ioDispatcher], keeping disk and network work off the
 * main thread and making the dispatcher swappable in tests.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dao: UserDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    override fun getUsers(): Flow<Resource<List<User>>> = flow {
        // 1. Surface the cached list instantly as a loading state.
        val cached = dao.observeUsers().first().toDomainList()
        emit(Resource.Loading(cached))

        // 2. Refresh from the network into the single source of truth.
        val errorMessage = runCatching {
            val remote = api.getUsers()
            dao.upsertAll(remote.toEntities(currentTimeMillis()))
        }.exceptionOrNull()?.toUserMessage()

        // 3. Observe the database; re-emits on every subsequent write.
        emitAll(
            dao.observeUsers().map { entities ->
                val users = entities.toDomainList()
                if (errorMessage != null) {
                    Resource.Error(message = errorMessage, data = users)
                } else {
                    Resource.Success(users)
                }
            }
        )
    }.flowOn(ioDispatcher)

    override fun getUserById(id: Int): Flow<Resource<User>> = flow {
        val cached = dao.observeUserById(id).first()?.toDomain()
        emit(Resource.Loading(cached))

        val errorMessage = runCatching {
            val remote = api.getUserById(id)
            dao.upsert(remote.toEntity(currentTimeMillis()))
        }.exceptionOrNull()?.toUserMessage()

        emitAll(
            dao.observeUserById(id).map { entity ->
                val user = entity?.toDomain()
                when {
                    user != null && errorMessage == null -> Resource.Success(user)
                    user != null -> Resource.Error(message = errorMessage!!, data = user)
                    else -> Resource.Error(
                        message = errorMessage ?: ERROR_USER_NOT_FOUND,
                    )
                }
            }
        )
    }.flowOn(ioDispatcher)

    /** Extracted so it can be overridden/stubbed in tests; wraps the platform clock. */
    private fun currentTimeMillis(): Long = System.currentTimeMillis()

    /** Translates known network failures into user-facing messages. */
    private fun Throwable.toUserMessage(): String = when (this) {
        is IOException -> ERROR_NO_CONNECTION
        is HttpException -> "$ERROR_SERVER (${code()})"
        else -> message ?: ERROR_UNKNOWN
    }

    private companion object {
        const val ERROR_NO_CONNECTION = "No internet connection. Showing cached data."
        const val ERROR_SERVER = "Server error"
        const val ERROR_UNKNOWN = "An unexpected error occurred."
        const val ERROR_USER_NOT_FOUND = "User not found."
    }
}
