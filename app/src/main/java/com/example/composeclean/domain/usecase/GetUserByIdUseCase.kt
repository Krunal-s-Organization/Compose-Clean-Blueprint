package com.example.composeclean.domain.usecase

import com.example.composeclean.core.common.Resource
import com.example.composeclean.domain.model.User
import com.example.composeclean.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that emits a single user identified by [id] as a stream of [Resource]s.
 *
 * @see GetUsersUseCase for the rationale behind the one-action-per-use-case convention.
 */
class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    /**
     * @param id the unique identifier of the user to observe.
     */
    operator fun invoke(id: Int): Flow<Resource<User>> = repository.getUserById(id)
}
