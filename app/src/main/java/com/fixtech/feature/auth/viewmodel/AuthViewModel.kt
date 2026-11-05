package com.fixtech.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // User Login
    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    // User Register
    fun onRegisterFullNameChanged(name: String) {
        _uiState.update { it.copy(registerFullName = name) }
    }

    fun onRegisterEmailChanged(email: String) {
        _uiState.update { it.copy(registerEmail = email) }
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.update { it.copy(registerPassword = password) }
    }

    // Tech Login
    fun onTechEmailChanged(email: String) {
        _uiState.update { it.copy(techEmail = email) }
    }

    fun onTechPasswordChanged(password: String) {
        _uiState.update { it.copy(techPassword = password) }
    }

    // Tech Register
    fun onTechFullNameChanged(name: String) {
        _uiState.update { it.copy(techFullName = name) }
    }

    fun onTechRegisterEmailChanged(email: String) {
        _uiState.update { it.copy(techRegisterEmail = email) }
    }

    fun onWorkshopCodeChanged(code: String) {
        _uiState.update { it.copy(workshopCode = code) }
    }

    fun onTechRegisterPasswordChanged(password: String) {
        _uiState.update { it.copy(techRegisterPassword = password) }
    }

    fun sendWorkshopVerificationCode() {
        // Trigger verification code sending logic
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
