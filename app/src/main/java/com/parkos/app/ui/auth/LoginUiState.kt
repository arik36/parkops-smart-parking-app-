package com.parkos.app.ui.auth

import com.parkos.app.domain.model.User

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userType: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}