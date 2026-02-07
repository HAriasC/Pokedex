package com.bks.pokedex.ui.screens.login

interface LoginContract {
    data class State(
        val user: String = "",
        val pass: String = "",
        val isLoading: Boolean = false,
        val loadingMessage: String = "Loading...",
        val error: String? = null,
        val isBiometricAvailable: Boolean = false
    )

    sealed class Intent {
        data class OnUserChange(val user: String) : Intent()
        data class OnPassChange(val pass: String) : Intent()
        object OnLoginClick : Intent()
        object OnBiometricLoginClick : Intent()
    }

    sealed class Effect {
        object NavigateToHome : Effect()
    }
}
