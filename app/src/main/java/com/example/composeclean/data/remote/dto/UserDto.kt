package com.example.composeclean.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network transfer object for a user, shaped to match the JSON returned by the API.
 *
 * It is annotated for kotlinx.serialization and kept separate from the domain [User] model so that
 * changes to the wire format never leak into business logic. The default sample API
 * (jsonplaceholder.typicode.com) does not provide an avatar, so [avatarUrl] is nullable.
 */
@Serializable
data class UserDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("avatarUrl") val avatarUrl: String? = null,
)
