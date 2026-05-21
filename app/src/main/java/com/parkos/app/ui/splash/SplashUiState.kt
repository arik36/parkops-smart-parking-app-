package com.parkos.app.ui.splash

sealed class SplashUiState {

    object Loading : SplashUiState()

    object NotLogged : SplashUiState()

    data class Logged(
        val role: String
    ) : SplashUiState()
}