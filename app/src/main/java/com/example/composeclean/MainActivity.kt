package com.example.composeclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composeclean.designsystem.theme.ComposeCleanTheme
import com.example.composeclean.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity entry point for the application.
 *
 * The [AndroidEntryPoint] annotation makes this activity a member of Hilt's dependency graph so
 * that any `@HiltViewModel` reached through Navigation Compose can be injected. All UI is hosted
 * inside Compose; there are no XML layouts or fragments.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCleanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}
