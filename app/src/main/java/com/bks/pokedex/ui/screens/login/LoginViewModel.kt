package com.bks.pokedex.ui.screens.login

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bks.pokedex.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val loginUseCase: LoginUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LoginContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        checkBiometricAvailability()
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)

        _state.update {
            it.copy(isBiometricAvailable = canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS)
        }
    }

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
                            loadingMessage = "Verificando identidad biométrica..."
                        )
                    }
                    loginUseCase("biometric_user", "biometric_pass")
                        .onSuccess {
                            simulateSuccessfulLogin(isBiometric = true)
                        }
                        .onFailure { error ->
                            _state.update { it.copy(isLoading = false, error = error.message) }
                        }
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loadingMessage = "Iniciando sesión...") }

            loginUseCase(_state.value.user, _state.value.pass)
                .onSuccess {
                    simulateSuccessfulLogin(isBiometric = false)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    /**
     * Maneja la experiencia visual de bienvenida tras una autenticación exitosa en el repositorio.
     */
    private suspend fun simulateSuccessfulLogin(isBiometric: Boolean) {
        _state.update { it.copy(loadingMessage = "Sincronizando Pokédex...") }
        delay(1000)

        val welcomeName = if (isBiometric) "Entrenador" else _state.value.user
        _state.update { it.copy(loadingMessage = "¡Bienvenido, $welcomeName!") }

        delay(1500)
        _effect.send(LoginContract.Effect.NavigateToHome)

        delay(500)
        _state.update { it.copy(isLoading = false) }
    }
}
