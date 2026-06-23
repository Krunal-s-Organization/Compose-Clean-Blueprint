package com.example.composeclean.data.remote.api

import com.example.composeclean.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit description of the user-related HTTP endpoints.
 *
 * Methods are `suspend` so Retrofit drives them on a background dispatcher and returns the parsed
 * body directly. Thrown [retrofit2.HttpException] / [java.io.IOException] are caught and translated
 * into [com.example.composeclean.core.common.Resource.Error] by the repository.
 */
interface UserApi {

    /** GET /users — returns the full list of users. */
    @GET(PATH_USERS)
    suspend fun getUsers(): List<UserDto>

    /** GET /users/{id} — returns a single user. */
    @GET("$PATH_USERS/{$PARAM_ID}")
    suspend fun getUserById(@Path(PARAM_ID) id: Int): UserDto

    companion object {
        private const val PATH_USERS = "users"
        private const val PARAM_ID = "id"
    }
}
