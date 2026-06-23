package com.example.composeclean.presentation.mvvm.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeclean.R
import com.example.composeclean.domain.model.User
import com.example.composeclean.ui.theme.ComposeCleanTheme

/**
 * Reusable Material 3 [Card] that renders a single [User] in a list.
 *
 * Stateless and side-effect free: it takes the data plus an [onClick] callback, so it can be reused
 * by both the MVVM and MVI screens and is trivial to preview/test.
 *
 * @param user the user to display.
 * @param onClick invoked when the card is tapped, typically to navigate to the detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = stringResource(R.string.content_desc_user_avatar),
                contentScale = ContentScale.Crop,
                error = rememberAvatarFallback(),
                placeholder = rememberAvatarFallback(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** A simple vector fallback used while the avatar loads or when no URL is available. */
@Composable
private fun rememberAvatarFallback() =
    androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Filled.Person)

@Preview(showBackground = true)
@Composable
private fun UserCardPreview() {
    ComposeCleanTheme {
        UserCard(
            user = User(
                id = 1,
                name = "Ada Lovelace",
                email = "ada@example.com",
                phone = "+1 555 0100",
                avatarUrl = null,
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
