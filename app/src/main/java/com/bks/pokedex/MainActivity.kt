package com.bks.pokedex

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bks.pokedex.ui.screens.detail.DetailScreen
import com.bks.pokedex.ui.screens.home.HomeScreen
import com.bks.pokedex.ui.screens.favorites.FavoritesScreen
import com.bks.pokedex.ui.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Favorites : BottomNavItem(
        "favorites",
        "Favorites",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder
    )
}

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
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val bottomBarVisible = currentDestination?.route in listOf(
                    BottomNavItem.Home.route,
                    BottomNavItem.Favorites.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFDC0A2D),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = bottomBarVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            CompactPokedexBottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    SharedTransitionLayout {
                        NavHost(
                            navController = navController,
                            startDestination = BottomNavItem.Home.route,
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            composable(BottomNavItem.Home.route) {
                                HomeScreen(
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@composable,
                                    onNavigateToDetail = { pokemonName, pokemonId ->
                                        navController.navigate("detail/$pokemonName/$pokemonId")
                                    },
                                    onNavigateToFavorites = {
                                        navController.navigate(BottomNavItem.Favorites.route)
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
                            composable(BottomNavItem.Favorites.route) {
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
}

@Composable
fun CompactPokedexBottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        color = Color(0xFFDC0A2D),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                val contentColor by animateColorAsState(
                    targetValue = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
                    label = "color"
                )
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.1f else 1.0f,
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(
                                bounded = true,
                                color = Color.White
                            )
                        ) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = contentColor,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(iconScale)
                        )
                        Text(
                            text = item.title,
                            color = contentColor,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
