package com.example.composeclean.core.common

/**
 * A generic, immutable wrapper describing the state of a single screen for the UI layer.
 *
 * Unlike [Resource], which models the *result of one operation*, [UiState] is intended to be held
 * in a ViewModel's `StateFlow` and rendered directly by a composable. It separates the three
 * concerns a screen almost always needs: whether data is loading, the data itself, and an optional
 * error message.
 *
 * @param T the type of the screen's content.
 * @property isLoading whether a load/refresh is currently in progress.
 * @property data the content to render, or `null` before the first successful load.
 * @property errorMessage a user-facing error message, or `null` when there is no error.
 */
data class UiState<out T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorMessage: String? = null,
) {
    /** `true` when there is neither data nor an in-flight load — useful for empty-state UI. */
    val isEmpty: Boolean
        get() = !isLoading && data == null && errorMessage == null

    companion object {
        /** Convenience factory for the initial loading state of a screen. */
        fun <T> loading(previous: T? = null): UiState<T> =
            UiState(isLoading = true, data = previous)
    }
}

/** Folds a [Resource] into the corresponding [UiState], preserving any cached data. */
fun <T> Resource<T>.toUiState(): UiState<T> = when (this) {
    is Resource.Success -> UiState(isLoading = false, data = data)
    is Resource.Loading -> UiState(isLoading = true, data = data)
    is Resource.Error -> UiState(isLoading = false, data = data, errorMessage = message)
}
