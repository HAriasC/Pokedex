package com.bks.pokedex

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bks.pokedex.domain.usecase.IsLoggedInUseCase
import com.bks.pokedex.ui.navigation.BottomNavItem
import com.bks.pokedex.ui.navigation.PokedexBottomBar
import com.bks.pokedex.ui.navigation.PokedexNavHost
import com.bks.pokedex.ui.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var isLoggedInUseCase: IsLoggedInUseCase

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

        setupSplashExitAnimation(splashScreen)
        enableEdgeToEdge()

        setContent {
            PokedexTheme {
                val isLoggedIn by isLoggedInUseCase().collectAsState(initial = null)
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (isLoggedIn == null) return@PokedexTheme

                val isBottomBarVisible = currentRoute in listOf(
                    BottomNavItem.Home.route,
                    BottomNavItem.Favorites.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFDC0A2D),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = isBottomBarVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            PokedexBottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    SharedTransitionLayout {
                        PokedexNavHost(
                            navController = navController,
                            isLoggedIn = isLoggedIn ?: false,
                            sharedTransitionScope = this,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }

    private fun setupSplashExitAnimation(splashScreen: androidx.core.splashscreen.SplashScreen) {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView
            lifecycleScope.launch {
                val loggedIn = isLoggedInUseCase().first()
                val animatorSet = AnimatorSet()

                if (loggedIn) {
                    val moveX = ObjectAnimator.ofFloat(iconView, View.TRANSLATION_X, 0f, -400f)
                    val moveY = ObjectAnimator.ofFloat(iconView, View.TRANSLATION_Y, 0f, -800f)
                    val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 0.4f)
                    val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 0.4f)
                    val alphaIcon = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)
                    val alphaBackground =
                        ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
                    animatorSet.playTogether(
                        moveX,
                        moveY,
                        scaleX,
                        scaleY,
                        alphaIcon,
                        alphaBackground
                    )
                } else {
                    val moveY = ObjectAnimator.ofFloat(iconView, View.TRANSLATION_Y, 0f, -150f)
                    val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 1.2f)
                    val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 1.2f)
                    val alphaBackground =
                        ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
                    val alphaIcon = ObjectAnimator.ofFloat(iconView, View.ALPHA, 1f, 0f)
                    animatorSet.playTogether(moveY, scaleX, scaleY, alphaBackground, alphaIcon)
                }

                animatorSet.apply {
                    duration = 800L
                    interpolator = AnticipateInterpolator()
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }
    }
}
