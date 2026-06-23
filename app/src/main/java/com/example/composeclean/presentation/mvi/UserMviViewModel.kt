package com.example.composeclean.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeclean.core.common.Resource
import com.example.composeclean.domain.model.User
import com.example.composeclean.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * MVI ViewModel for the user list screen.
 *
 * ### Pattern notes (MVI)
 *  - **Unidirectional data flow:** the UI sends [UserIntent]s to [onIntent]; the ViewModel reduces
 *    them into a new [UserState] exposed via [state]; the UI re-renders from that single state.
 *  - **State vs. effects:** persistent, re-renderable data lives in [state]; one-shot events
 *    (navigation, snackbars) are emitted through a [Channel] exposed as [effects], so they fire
 *    exactly once and never replay on recomposition or configuration change.
 *  - All business logic is delegated to [GetUsersUseCase]; the ViewModel only reduces its
 *    [Resource] emissions into UI state.
 */
@HiltViewModel
class UserMviViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserState(isLoading = true))
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _effects = Channel<UserEffect>(Channel.BUFFERED)

    /** A cold stream of one-time effects; collect it once from the screen. */
    val effects: Flow<UserEffect> = _effects.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        onIntent(UserIntent.LoadUsers)
    }

    /** Single entry point for everything the UI can do. */
    fun onIntent(intent: UserIntent) {
        when (intent) {
            UserIntent.LoadUsers, UserIntent.Refresh -> loadUsers()
            is UserIntent.UserClicked -> emitEffect(UserEffect.NavigateToDetail(intent.userId))
        }
    }

    private fun loadUsers() {
        // Cancel any in-flight collection so a refresh supersedes the previous load.
        loadJob?.cancel()
        loadJob = getUsersUseCase()
            .onEach(::reduce)
            .launchIn(viewModelScope)
    }

    /** Pure-ish reducer: maps a [Resource] emission into the next [UserState] (+ optional effect). */
    private fun reduce(resource: Resource<List<User>>) {
        when (resource) {
            is Resource.Loading -> _state.update { current ->
                current.copy(
                    isLoading = true,
                    users = resource.data ?: current.users,
                    errorMessage = null,
                )
            }

            is Resource.Success -> _state.update { current ->
                current.copy(
                    isLoading = false,
                    users = resource.data,
                    errorMessage = null,
                )
            }

            is Resource.Error -> {
                val fallback = resource.data ?: _state.value.users
                // If we still have data to show, surface the failure as a transient message
                // rather than blocking the whole screen with an error state.
                if (fallback.isNotEmpty()) {
                    emitEffect(UserEffect.ShowMessage(resource.message))
                }
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        users = fallback,
                        errorMessage = resource.message,
                    )
                }
            }
        }
    }

    private fun emitEffect(effect: UserEffect) {
        _effects.trySend(effect)
    }
}
