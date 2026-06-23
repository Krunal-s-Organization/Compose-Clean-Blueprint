package com.example.composeclean.presentation.mvi

import androidx.compose.runtime.Immutable
import com.example.composeclean.domain.model.User

/**
 * The single, immutable state object the MVI user screen renders.
 *
 * Everything the UI needs to draw itself lives here — there is exactly one source of truth. Marked
 * [Immutable] so Compose can skip recomposition when the reference is unchanged.
 *
 * @property isLoading whether a load/refresh is in progress.
 * @property users the list to display (may be stale cached data during an error).
 * @property errorMessage a user-facing error message, or `null` when there is no error.
 */
@Immutable
data class UserState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val errorMessage: String? = null,
) {
    /** `true` when a successful, non-loading load produced no users. */
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && users.isEmpty()

    /** `true` when there is no data to show and an error occurred — render the full-screen error. */
    val isFatalError: Boolean
        get() = errorMessage != null && users.isEmpty()
}
