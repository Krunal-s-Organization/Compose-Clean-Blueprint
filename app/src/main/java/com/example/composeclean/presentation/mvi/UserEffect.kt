package com.example.composeclean.presentation.mvi

/**
 * One-time side effects emitted by the MVI ViewModel.
 *
 * Effects model things that should happen exactly once and must *not* be part of the persistent
 * [UserState] (otherwise they'd replay on every recomposition or config change). Examples: showing
 * a snackbar/toast or performing navigation. They are delivered through a `Channel` so each effect
 * is consumed by a single collector.
 */
sealed interface UserEffect {

    /**
     * Navigate to the detail screen for [userId].
     *
     * @property userId the id of the user to open.
     */
    data class NavigateToDetail(val userId: Int) : UserEffect

    /**
     * Show a transient message (e.g. snackbar) — used for non-fatal refresh failures.
     *
     * @property message the text to display.
     */
    data class ShowMessage(val message: String) : UserEffect
}
