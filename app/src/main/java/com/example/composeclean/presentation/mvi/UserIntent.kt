package com.example.composeclean.presentation.mvi

/**
 * The set of user intents (actions) the MVI user screen can receive.
 *
 * In MVI the UI is a pure function of state, and the *only* way to change state is by dispatching
 * an [UserIntent] to the ViewModel. Modelling intents as a sealed interface gives an exhaustive,
 * compile-checked list of everything that can happen on the screen.
 */
sealed interface UserIntent {

    /** Initial load of the user list (typically dispatched once on first composition). */
    data object LoadUsers : UserIntent

    /** Pull-to-refresh / retry — re-runs the load. */
    data object Refresh : UserIntent

    /**
     * The user tapped a row.
     *
     * @property userId the id of the tapped user.
     */
    data class UserClicked(val userId: Int) : UserIntent
}
