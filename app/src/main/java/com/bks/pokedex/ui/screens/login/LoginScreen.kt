package com.bks.pokedex.ui.screens.login

import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.bks.pokedex.ui.screens.login.components.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    sharedTransitionScope: SharedTransitionScope,
    animVisibilityScope: AnimatedVisibilityScope,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val rotation = remember { Animatable(0f) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginContract.Effect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val backgroundBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFE33545), Color(0xFFDC0A2D)),
        center = androidx.compose.ui.geometry.Offset(x = 500f, y = 200f),
        radius = 1500f
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenHeight = maxHeight

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = screenHeight),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        LoginHeader(
                            rotationValue = rotation.value,
                            sharedTransitionScope = sharedTransitionScope,
                            animVisibilityScope = animVisibilityScope
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        LoginForm(
                            user = state.user,
                            pass = state.pass,
                            error = state.error,
                            isLoading = state.isLoading,
                            onUserChange = { viewModel.onIntent(LoginContract.Intent.OnUserChange(it)) },
                            onPassChange = { viewModel.onIntent(LoginContract.Intent.OnPassChange(it)) },
                            onLoginClick = {
                                focusManager.clearFocus()
                                viewModel.onIntent(LoginContract.Intent.OnLoginClick)
                            },
                            onBiometricClick = {
                                val fragmentActivity = context as? FragmentActivity
                                if (fragmentActivity != null) {
                                    handleBiometricLogin(fragmentActivity) {
                                        viewModel.onIntent(LoginContract.Intent.OnBiometricLoginClick)
                                    }
                                }
                            },
                            isBiometricAvailable = state.isBiometricAvailable
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        LoginFooter()

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            if (state.isLoading) {
                LoadingOverlay(
                    message = state.loadingMessage,
                    sharedTransitionScope = sharedTransitionScope,
                    animVisibilityScope = animVisibilityScope,
                )
            }
        }
    }
}

private fun handleBiometricLogin(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }
        }
    )
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Login")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Cancel").build()
    biometricPrompt.authenticate(promptInfo)
}
