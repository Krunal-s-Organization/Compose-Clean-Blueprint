package com.example.composeclean.data.mapper

import com.example.composeclean.data.local.entity.UserEntity
import com.example.composeclean.data.remote.dto.UserDto
import com.example.composeclean.domain.model.User

/**
 * Pure mapping functions between the three representations of a user:
 *
 * ```
 * UserDto  (network)  ──toEntity()──▶  UserEntity (database)
 * UserEntity (database) ──toDomain()─▶  User       (domain)
 * UserDto  (network)  ──toDomain()──▶  User       (domain)
 * ```
 *
 * Keeping mapping in one file (rather than scattered across classes) makes the data-flow obvious
 * and the conversions trivial to unit-test. The functions are extension functions so call sites
 * read naturally, e.g. `dto.toEntity(now)`.
 */

/** Maps a network [UserDto] into a persistable [UserEntity], stamping it with [cachedAt]. */
fun UserDto.toEntity(cachedAt: Long): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    phone = phone.orEmpty(),
    avatarUrl = avatarUrl,
    cachedAt = cachedAt,
)

/** Maps a cached [UserEntity] into the framework-free domain [User]. */
fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    phone = phone,
    avatarUrl = avatarUrl,
)

/** Maps a network [UserDto] straight to the domain [User] (used when bypassing the cache). */
fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    phone = phone.orEmpty(),
    avatarUrl = avatarUrl,
)

/** Convenience: maps a list of DTOs into entities sharing the same [cachedAt] timestamp. */
fun List<UserDto>.toEntities(cachedAt: Long): List<UserEntity> = map { it.toEntity(cachedAt) }

/** Convenience: maps a list of entities into domain models. */
fun List<UserEntity>.toDomainList(): List<User> = map { it.toDomain() }
