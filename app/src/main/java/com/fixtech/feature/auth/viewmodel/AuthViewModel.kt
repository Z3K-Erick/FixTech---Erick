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
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = if (email.trim().isNotEmpty()) false else it.isEmailError
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isPasswordError = if (password.trim().isNotEmpty()) false else it.isPasswordError
            )
        }
    }

    fun validateUserLogin(): Boolean {
        val current = _uiState.value
        val emailEmpty = current.email.trim().isEmpty()
        val passwordEmpty = current.password.trim().isEmpty()
        _uiState.update {
            it.copy(
                isEmailError = emailEmpty,
                isPasswordError = passwordEmpty
            )
        }
        return !emailEmpty && !passwordEmpty
    }

    // User Register
    fun onRegisterFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                registerFullName = name,
                isRegisterFullNameError = if (name.trim().isNotEmpty()) false else it.isRegisterFullNameError
            )
        }
    }

    fun onRegisterEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                registerEmail = email,
                isRegisterEmailError = if (email.trim().isNotEmpty()) false else it.isRegisterEmailError
            )
        }
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                registerPassword = password,
                isRegisterPasswordError = if (password.trim().isNotEmpty()) false else it.isRegisterPasswordError
            )
        }
    }

    fun validateUserRegister(): Boolean {
        val current = _uiState.value
        val nameEmpty = current.registerFullName.trim().isEmpty()
        val emailEmpty = current.registerEmail.trim().isEmpty()
        val passwordEmpty = current.registerPassword.trim().isEmpty()
        _uiState.update {
            it.copy(
                isRegisterFullNameError = nameEmpty,
                isRegisterEmailError = emailEmpty,
                isRegisterPasswordError = passwordEmpty
            )
        }
        return !nameEmpty && !emailEmpty && !passwordEmpty
    }

    // Tech Login
    fun onTechEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                techEmail = email,
                isTechEmailError = if (email.trim().isNotEmpty()) false else it.isTechEmailError
            )
        }
    }

    fun onTechPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                techPassword = password,
                isTechPasswordError = if (password.trim().isNotEmpty()) false else it.isTechPasswordError
            )
        }
    }

    fun validateTechLogin(): Boolean {
        val current = _uiState.value
        val emailEmpty = current.techEmail.trim().isEmpty()
        val passwordEmpty = current.techPassword.trim().isEmpty()
        _uiState.update {
            it.copy(
                isTechEmailError = emailEmpty,
                isTechPasswordError = passwordEmpty
            )
        }
        return !emailEmpty && !passwordEmpty
    }

    // Tech Register
    fun onTechFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                techFullName = name,
                isTechFullNameError = if (name.trim().isNotEmpty()) false else it.isTechFullNameError
            )
        }
    }

    fun onTechRegisterEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                techRegisterEmail = email,
                isTechRegisterEmailError = if (email.trim().isNotEmpty()) false else it.isTechRegisterEmailError
            )
        }
    }

    fun onWorkshopCodeChanged(code: String) {
        _uiState.update {
            it.copy(
                workshopCode = code,
                isWorkshopCodeError = if (code.trim().isNotEmpty()) false else it.isWorkshopCodeError
            )
        }
    }

    fun onTechRegisterPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                techRegisterPassword = password,
                isTechRegisterPasswordError = if (password.trim().isNotEmpty()) false else it.isTechRegisterPasswordError
            )
        }
    }

    fun validateTechRegister(): Boolean {
        val current = _uiState.value
        val nameEmpty = current.techFullName.trim().isEmpty()
        val emailEmpty = current.techRegisterEmail.trim().isEmpty()
        val codeEmpty = current.workshopCode.trim().isEmpty()
        val passwordEmpty = current.techRegisterPassword.trim().isEmpty()
        _uiState.update {
            it.copy(
                isTechFullNameError = nameEmpty,
                isTechRegisterEmailError = emailEmpty,
                isWorkshopCodeError = codeEmpty,
                isTechRegisterPasswordError = passwordEmpty
            )
        }
        return !nameEmpty && !emailEmpty && !codeEmpty && !passwordEmpty
    }

    fun sendWorkshopVerificationCode() {
        // Trigger verification code sending logic
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
