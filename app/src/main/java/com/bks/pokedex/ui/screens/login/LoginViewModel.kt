package com.bks.pokedex.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bks.pokedex.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.OnUserChange -> {
                _state.update { it.copy(user = intent.user, error = null) }
            }

            is LoginContract.Intent.OnPassChange -> {
                _state.update { it.copy(pass = intent.pass, error = null) }
            }

            LoginContract.Intent.OnLoginClick -> login()
            LoginContract.Intent.OnBiometricLoginClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            loadingMessage = "Biometric data verified..."
                        )
                    }
                    delay(1000)
                    loginUseCase("biometric_user", "biometric_pass")
                    simulateSuccessfulLogin(isBiometric = true)
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loadingMessage = "Verifying credentials...") }
            delay(1000)
            loginUseCase(_state.value.user, _state.value.pass)
                .onSuccess {
                    simulateSuccessfulLogin(isBiometric = false)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private suspend fun simulateSuccessfulLogin(isBiometric: Boolean) {
        _state.update { it.copy(isLoading = true, loadingMessage = "Loading Pokédex data...") }
        delay(1500)

        val welcomeName = if (isBiometric) "User" else _state.value.user
        _state.update { it.copy(loadingMessage = "Welcome, $welcomeName!") }

        delay(1500)
        _effect.send(LoginContract.Effect.NavigateToHome)

        delay(500)
        _state.update { it.copy(isLoading = false) }
    }
}
