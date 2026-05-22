package com.parkos.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkos.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _organizationIds = MutableStateFlow<List<String>>(emptyList())
    val organizationIds = _organizationIds.asStateFlow()

    private val _organizationError = MutableStateFlow<String?>(null)
    val organizationError = _organizationError.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val result = authRepository.login(email, password)

            result.onSuccess { user ->
                _uiState.value = LoginUiState.Success(user.userType)
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(
                    error.message ?: "Error desconocido"
                )
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        orgId: String?
    ) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val result = authRepository.register(
                fullName = fullName,
                email = email,
                password = password,
                orgId = orgId
            )

            result.onSuccess { user ->
                _uiState.value = LoginUiState.Success(user.userType)
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(
                    error.message ?: "Error desconocido"
                )
            }
        }
    }

    fun loadOrganizationIds() {
        viewModelScope.launch {
            val result = authRepository.getOrganizationIds()

            result.onSuccess { ids ->
                _organizationIds.value = ids
                _organizationError.value = null
            }.onFailure { error ->
                _organizationError.value =
                    error.message ?: "No se pudieron cargar los IDs"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = LoginUiState.Idle
        }
    }

    fun clearError() {
        _uiState.value = LoginUiState.Idle
    }
}