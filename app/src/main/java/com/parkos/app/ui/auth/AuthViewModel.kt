package com.parkos.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkos.app.core.TokenManager
import com.parkos.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<LoginUiState>(
            LoginUiState.Idle
        )

    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {

        viewModelScope.launch {

            _uiState.value = LoginUiState.Loading

            val result =
                authRepository.login(email, password)

            result.onSuccess { user ->

                val userType = user.userType

                _uiState.value =
                    LoginUiState.Success(userType)

            }.onFailure { error ->

                _uiState.value =
                    LoginUiState.Error(
                        error.message ?: "Error desconocido"
                    )
            }
        }
    }

    fun logout() {

        viewModelScope.launch {

            tokenManager.clearSession()

            _uiState.value =
                LoginUiState.Idle
        }
    }
}