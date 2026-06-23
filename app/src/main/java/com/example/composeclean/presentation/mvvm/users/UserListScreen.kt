package com.example.composeclean.presentation.mvvm.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeclean.R
import com.example.composeclean.core.common.UiState
import com.example.composeclean.domain.model.User
import com.example.composeclean.presentation.mvvm.components.EmptyState
import com.example.composeclean.presentation.mvvm.components.ErrorState
import com.example.composeclean.presentation.mvvm.components.LoadingIndicator
import com.example.composeclean.presentation.mvvm.components.UserCard

/**
 * MVVM user-list screen.
 *
 * This is the stateful entry point: it obtains the [UserListViewModel] via Hilt and collects its
 * state in a lifecycle-aware way (`collectAsStateWithLifecycle`, never the raw `collectAsState`),
 * then delegates rendering to the stateless [UserListContent] for easy previewing/testing.
 *
 * @param onUserClick navigation callback fired with the tapped user's id.
 */
@Composable
fun UserListScreen(
    onUserClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UserListContent(
        uiState = uiState,
        onUserClick = onUserClick,
        onRefresh = viewModel::refresh,
        modifier = modifier,
    )
}

/**
 * Stateless content for the user list. Renders one of four states — loading, error, empty, or the
 * list — and supports pull-to-refresh in every state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListContent(
    uiState: UiState<List<User>>,
    onUserClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_user_list_mvvm)) })
        },
    ) { innerPadding ->
        val users = uiState.data
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                // First load with nothing cached yet.
                uiState.isLoading && users.isNullOrEmpty() -> LoadingIndicator()

                // Hard error and no cached data to fall back on.
                uiState.errorMessage != null && users.isNullOrEmpty() -> ErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRefresh,
                )

                // Successful load but the list is empty.
                users.isNullOrEmpty() -> EmptyState(
                    message = stringResource(R.string.state_empty_users),
                )

                // We have data (possibly stale alongside an error) — show it.
                else -> UserList(users = users, onUserClick = onUserClick)
            }
        }
    }
}

@Composable
private fun UserList(
    users: List<User>,
    onUserClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(items = users, key = { user -> user.id }) { user ->
            Box(modifier = Modifier.padding(bottom = 12.dp)) {
                UserCard(user = user, onClick = { onUserClick(user.id) })
            }
        }
    }
}
