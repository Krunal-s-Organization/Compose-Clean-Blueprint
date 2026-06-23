package com.example.composeclean.presentation.mvvm.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeclean.core.common.UiState
import com.example.composeclean.core.common.toUiState
import com.example.composeclean.domain.model.User
import com.example.composeclean.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * MVVM ViewModel for the user list screen.
 *
 * ### Pattern notes (MVVM)
 *  - Exposes a single immutable [UiState] via a read-only [StateFlow]; the UI observes it with
 *    `collectAsStateWithLifecycle()` and renders declaratively.
 *  - Business logic is delegated to [GetUsersUseCase]; the ViewModel only adapts the resulting
 *    [com.example.composeclean.core.common.Resource] stream into [UiState] for the view.
 *  - A [refreshTrigger] backed by `flatMapLatest` re-subscribes to the use case on demand
 *    (pull-to-refresh / retry), cancelling any in-flight collection.
 *  - `stateIn(WhileSubscribed)` keeps the upstream alive only while the screen is visible (+5s for
 *    config changes), so we don't keep hitting the DB/network when the screen is in the background.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<List<User>>> = refreshTrigger
        .flatMapLatest { getUsersUseCase() }
        .map { resource -> resource.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.loading(),
        )

    /** Re-runs the offline-first load. Called from pull-to-refresh and the error retry button. */
    fun refresh() {
        refreshTrigger.update { it + 1 }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
