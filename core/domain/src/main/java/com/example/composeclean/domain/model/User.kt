package com.example.composeclean.domain.model

/**
 * Pure domain representation of a user.
 *
 * This model is deliberately free of any framework annotations (no Room, no Retrofit/serialization)
 * so the domain layer stays independent of how data is stored or transported. Mapping to and from
 * the persistence ([UserEntity]) and network ([UserDto]) models is handled in the data layer's
 * mappers.
 *
 * @property id stable unique identifier.
 * @property name full display name.
 * @property email contact email address.
 * @property phone contact phone number.
 * @property avatarUrl URL of the user's avatar image, if available.
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String?,
)
