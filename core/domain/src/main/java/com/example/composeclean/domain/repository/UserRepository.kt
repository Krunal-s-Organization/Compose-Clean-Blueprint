package com.example.composeclean.domain.repository

import com.example.composeclean.core.common.Resource
import com.example.composeclean.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level contract for accessing [User] data.
 *
 * The interface lives in the domain layer so that use cases depend on an abstraction, not on a
 * concrete data-source implementation (Dependency Inversion). The single implementation,
 * `UserRepositoryImpl`, lives in the data layer and is bound to this interface via Hilt.
 *
 * All read operations return a cold [Flow] of [Resource] so callers transparently receive cached
 * data first, a loading signal, and finally the freshly synced result — the offline-first contract.
 */
interface UserRepository {

    /**
     * Streams the full list of users using an offline-first strategy: the locally cached list is
     * emitted immediately, a network refresh is attempted, and the database (the single source of
     * truth) is observed for subsequent updates.
     */
    fun getUsers(): Flow<Resource<List<User>>>

    /**
     * Streams a single user by [id], backed by the local cache and refreshed from the network.
     *
     * @param id the unique identifier of the user to observe.
     */
    fun getUserById(id: Int): Flow<Resource<User>>
}
