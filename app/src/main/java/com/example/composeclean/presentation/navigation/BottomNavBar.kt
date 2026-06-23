package com.example.composeclean.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.composeclean.R

/**
 * The two top-level destinations surfaced in the bottom navigation bar. Each pairs a type-safe
 * [NavRoute] with the label/icon used to render its tab.
 */
enum class TopLevelDestination(
    val route: NavRoute,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Mvvm(NavRoute.UserListMvvm, R.string.nav_mvvm, Icons.AutoMirrored.Filled.List),
    Mvi(NavRoute.UserListMvi, R.string.nav_mvi, Icons.Filled.Dashboard),
}

/**
 * Bottom navigation bar that switches between the MVVM and MVI top-level destinations.
 *
 * Selection is derived from the live back stack so the highlighted tab always reflects the current
 * destination. Re-selecting tabs uses the standard single-top + save/restore-state pattern so each
 * tab keeps its own back stack and scroll position.
 *
 * @param navController the controller driving navigation.
 */
@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { navDest -> navDest.hasRoute(destination.route::class) } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        // Pop up to the graph start to avoid stacking destinations.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.labelRes),
                    )
                },
                label = { Text(stringResource(destination.labelRes)) },
            )
        }
    }
}
