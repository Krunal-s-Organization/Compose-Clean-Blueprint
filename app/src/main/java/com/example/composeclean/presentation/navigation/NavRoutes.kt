package com.example.composeclean.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations.
 *
 * With Navigation Compose 2.8+, routes are plain `@Serializable` types instead of hand-built string
 * patterns. Arguments become constructor parameters (see [UserDetail]), so the compiler — not a
 * runtime string parser — guarantees you pass the right data. Navigate with
 * `navController.navigate(NavRoute.UserDetail(userId = 1))` and read args via
 * `backStackEntry.toRoute<NavRoute.UserDetail>()`.
 */
sealed interface NavRoute {

    /** Users list rendered with the MVVM pattern. Top-level (bottom-nav) destination. */
    @Serializable
    data object UserListMvvm : NavRoute

    /** Users list rendered with the MVI pattern. Top-level (bottom-nav) destination. */
    @Serializable
    data object UserListMvi : NavRoute

    /**
     * Detail screen for a single user.
     *
     * @property userId the id of the user to display — a type-safe navigation argument.
     */
    @Serializable
    data class UserDetail(val userId: Int) : NavRoute
}
