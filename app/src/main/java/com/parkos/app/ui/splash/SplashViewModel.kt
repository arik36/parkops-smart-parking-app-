package com.parkos.app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkos.app.core.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SplashUiState>(
            SplashUiState.Loading
        )

    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val token = tokenManager.getTokenFlow().first()

            if (token.isNullOrBlank()) {
                _uiState.value = SplashUiState.NotLogged
                return@launch
            }

            if (tokenManager.isSessionExpired()) {
                tokenManager.clearSession()
                _uiState.value = SplashUiState.NotLogged
                return@launch
            }

            val role = tokenManager.getUserType()

            _uiState.value =
                SplashUiState.Logged(
                    role ?: "consumer"
                )
        }
    }
}