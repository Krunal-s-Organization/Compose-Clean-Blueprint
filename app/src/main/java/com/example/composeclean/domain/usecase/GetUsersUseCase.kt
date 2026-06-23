package com.example.composeclean.domain.usecase

import com.example.composeclean.core.common.Resource
import com.example.composeclean.domain.model.User
import com.example.composeclean.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that emits the list of users as a stream of [Resource]s.
 *
 * Use cases encapsulate a single business action and are the only thing the presentation layer
 * talks to. Keeping them tiny (one public [invoke]) makes ViewModels easy to read and the business
 * rules easy to test in isolation. This one is a thin pass-through today, but it is the natural
 * home for future rules such as filtering, sorting, or de-duplication.
 *
 * Implementing `operator fun invoke` lets callers use the instance like a function:
 * `getUsersUseCase()`.
 */
class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<User>>> = repository.getUsers()
}
