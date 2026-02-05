package com.bks.pokedex

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bks.pokedex.ui.screens.detail.DetailScreen
import com.bks.pokedex.ui.screens.home.HomeScreen
import com.bks.pokedex.ui.screens.favorites.FavoritesScreen
import com.bks.pokedex.ui.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        lifecycleScope.launch {
            delay(800)
            keepSplashScreen = false
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView
            val moveX = ObjectAnimator.ofFloat(iconView, View.TRANSLATION_X, 0f, -400f)
            val moveY = ObjectAnimator.ofFloat(iconView, View.TRANSLATION_Y, 0f, -800f)
            val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 0.4f)
            val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 0.4f)
            val alphaIcon = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)
            val alphaBackground = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

            AnimatorSet().apply {
                playTogether(moveX, moveY, scaleX, scaleY, alphaIcon, alphaBackground)
                duration = 600L
                interpolator = AnticipateInterpolator()
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }

        enableEdgeToEdge()

        setContent {
            PokedexTheme {
                val navController = rememberNavController()

                SharedTransitionLayout {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                onNavigateToDetail = { pokemonName, pokemonId ->
                                    navController.navigate("detail/$pokemonName/$pokemonId")
                                },
                                onNavigateToFavorites = {
                                    navController.navigate("favorites")
                                }
                            )
                        }
                        composable(
                            "detail/{pokemonName}/{pokemonId}",
                            arguments = listOf(
                                navArgument("pokemonName") { type = NavType.StringType },
                                navArgument("pokemonId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: 0
                            DetailScreen(
                                pokemonId = pokemonId,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("favorites") {
                            FavoritesScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                onNavigateToDetail = { pokemonName, pokemonId ->
                                    navController.navigate("detail/$pokemonName/$pokemonId")
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
