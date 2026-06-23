package com.example.composeclean.feature.users.mvvm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.composeclean.core.common.UiState
import com.example.composeclean.designsystem.components.ErrorState
import com.example.composeclean.designsystem.components.LoadingIndicator
import com.example.composeclean.designsystem.R as DesignSystemR
import com.example.composeclean.domain.model.User

/**
 * MVVM user-detail screen. Reads its id-bound [UserDetailViewModel] from Hilt and renders the
 * single user, with loading and error fallbacks.
 *
 * @param onBack invoked when the user taps the up/back affordance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_user_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        UserDetailBody(
            uiState = uiState,
            onRetry = { /* re-entering the screen re-triggers the flow; see ViewModel */ },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun UserDetailBody(
    uiState: UiState<User>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val user = uiState.data
    val errorMessage = uiState.errorMessage
    when {
        uiState.isLoading && user == null -> LoadingIndicator(modifier)
        errorMessage != null && user == null ->
            ErrorState(message = errorMessage, onRetry = onRetry, modifier = modifier)
        user != null -> UserDetailInfo(user = user, modifier = modifier)
    }
}

@Composable
private fun UserDetailInfo(
    user: User,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = stringResource(DesignSystemR.string.content_desc_user_avatar),
            contentScale = ContentScale.Crop,
            error = rememberVectorPainter(Icons.Filled.Person),
            placeholder = rememberVectorPainter(Icons.Filled.Person),
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape),
        )
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.label_email, user.email),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.label_phone, user.phone),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
