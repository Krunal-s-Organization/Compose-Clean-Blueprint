package com.example.composeclean.feature.users.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.composeclean.core.common.UiState
import com.example.composeclean.core.common.toUiState
import com.example.composeclean.core.navigation.NavRoute
import com.example.composeclean.domain.model.User
import com.example.composeclean.domain.usecase.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * MVVM ViewModel for the user detail screen.
 *
 * The user id is read from the type-safe navigation route via `SavedStateHandle.toRoute()`, so the
 * ViewModel never deals with raw string keys. Hilt injects the [SavedStateHandle] automatically.
 */
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserByIdUseCase: GetUserByIdUseCase,
) : ViewModel() {

    private val route: NavRoute.UserDetail = savedStateHandle.toRoute()

    val uiState: StateFlow<UiState<User>> = getUserByIdUseCase(route.userId)
        .map { resource -> resource.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.loading(),
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
