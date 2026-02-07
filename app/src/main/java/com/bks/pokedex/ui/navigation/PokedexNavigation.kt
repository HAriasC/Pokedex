package com.bks.pokedex.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bks.pokedex.ui.screens.detail.DetailScreen
import com.bks.pokedex.ui.screens.favorites.FavoritesScreen
import com.bks.pokedex.ui.screens.home.HomeScreen
import com.bks.pokedex.ui.screens.login.LoginScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Favorites : BottomNavItem(
        Screen.Favorites.route,
        "Favorites",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokedexNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    innerPadding: PaddingValues
) {
    with(sharedTransitionScope) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    sharedTransitionScope = this@with,
                    animVisibilityScope = this@composable,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    sharedTransitionScope = this@with,
                    animatedVisibilityScope = this@composable,
                    onNavigateToDetail = { pokemonName, pokemonId ->
                        navController.navigate(Screen.Detail.createRoute(pokemonName, pokemonId))
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    }
                )
            }
            composable(
                Screen.Detail.route,
                arguments = listOf(
                    navArgument("pokemonName") { type = NavType.StringType },
                    navArgument("pokemonId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
                val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: 0
                DetailScreen(
                    pokemonName = pokemonName,
                    pokemonId = pokemonId,
                    sharedTransitionScope = this@with,
                    animatedVisibilityScope = this@composable,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    sharedTransitionScope = this@with,
                    animatedVisibilityScope = this@composable,
                    onNavigateToDetail = { pokemonName, pokemonId ->
                        navController.navigate(Screen.Detail.createRoute(pokemonName, pokemonId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
