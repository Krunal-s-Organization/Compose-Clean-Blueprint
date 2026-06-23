package com.example.composeclean.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeclean.core.navigation.NavRoute
import com.example.composeclean.feature.users.mvi.UserMviScreen
import com.example.composeclean.feature.users.mvvm.UserDetailScreen
import com.example.composeclean.feature.users.mvvm.UserListScreen

/**
 * Root navigation graph for the app.
 *
 * Uses Navigation Compose's **type-safe** APIs: destinations are the `@Serializable` [NavRoute]
 * types, registered with `composable<Route>` and navigated to with `navigate(Route(args))`. The
 * two top-level destinations ([NavRoute.UserListMvvm] / [NavRoute.UserListMvi]) are hosted under a
 * [Scaffold] with a [BottomNavBar]; the detail screen is pushed on top without the bar.
 *
 * @param navController the controller hoisted so tests/hosts can drive navigation if needed.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.UserListMvvm,
            modifier = Modifier.padding(innerPadding),
        ) {
            // --- MVVM example -------------------------------------------------------------
            composable<NavRoute.UserListMvvm> {
                UserListScreen(
                    onUserClick = { userId ->
                        navController.navigate(NavRoute.UserDetail(userId))
                    },
                )
            }

            // --- MVI example --------------------------------------------------------------
            composable<NavRoute.UserListMvi> {
                UserMviScreen(
                    onNavigateToDetail = { userId ->
                        navController.navigate(NavRoute.UserDetail(userId))
                    },
                )
            }

            // --- Shared detail screen (reached from both patterns) ------------------------
            composable<NavRoute.UserDetail> {
                // UserDetailViewModel reads its `userId` argument from the type-safe route via
                // SavedStateHandle.toRoute(), so no argument is threaded through here.
                UserDetailScreen(
                    onBack = { navController.navigateUp() },
                )
            }
        }
    }
}
