package com.example.composeclean.presentation.mvi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeclean.R
import com.example.composeclean.presentation.mvvm.components.EmptyState
import com.example.composeclean.presentation.mvvm.components.ErrorState
import com.example.composeclean.presentation.mvvm.components.LoadingIndicator
import com.example.composeclean.presentation.mvvm.components.UserCard
import kotlinx.coroutines.flow.collectLatest

/**
 * MVI user-list screen.
 *
 * Demonstrates the full MVI loop:
 *  1. collects [UserState] with `collectAsStateWithLifecycle`;
 *  2. consumes one-time [UserEffect]s in a `LaunchedEffect` (navigation + snackbar);
 *  3. sends [UserIntent]s back to the ViewModel in response to user actions.
 *
 * @param onNavigateToDetail invoked (from an effect) when a user should be opened.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMviScreen(
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserMviViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Consume one-time effects. keyed to the ViewModel so it survives recomposition but restarts
    // if the ViewModel instance changes.
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UserEffect.NavigateToDetail -> onNavigateToDetail(effect.userId)
                is UserEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_user_list_mvi)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.onIntent(UserIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading && state.users.isEmpty() -> LoadingIndicator()

                state.isFatalError -> ErrorState(
                    message = state.errorMessage.orEmpty(),
                    onRetry = { viewModel.onIntent(UserIntent.Refresh) },
                )

                state.isEmpty -> EmptyState(
                    message = stringResource(R.string.state_empty_users),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(items = state.users, key = { user -> user.id }) { user ->
                        Box(modifier = Modifier.padding(bottom = 12.dp)) {
                            UserCard(
                                user = user,
                                onClick = { viewModel.onIntent(UserIntent.UserClicked(user.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}
