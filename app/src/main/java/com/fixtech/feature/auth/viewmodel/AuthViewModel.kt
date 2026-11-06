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
        val emailValid = current.email.isNotBlank()
        val passwordValid = current.password.isNotBlank()
        _uiState.update {
            it.copy(
                isEmailError = !emailValid,
                isPasswordError = !passwordValid
            )
        }
        return emailValid && passwordValid
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
        val nameValid = current.registerFullName.isNotBlank()
        val emailValid = current.registerEmail.isNotBlank()
        val passwordValid = current.registerPassword.isNotBlank()
        _uiState.update {
            it.copy(
                isRegisterFullNameError = !nameValid,
                isRegisterEmailError = !emailValid,
                isRegisterPasswordError = !passwordValid
            )
        }
        return nameValid && emailValid && passwordValid
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
        val emailValid = current.techEmail.isNotBlank()
        val passwordValid = current.techPassword.isNotBlank()
        _uiState.update {
            it.copy(
                isTechEmailError = !emailValid,
                isTechPasswordError = !passwordValid
            )
        }
        return emailValid && passwordValid
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
        val nameValid = current.techFullName.isNotBlank()
        val emailValid = current.techRegisterEmail.isNotBlank()
        val codeValid = current.workshopCode.isNotBlank()
        val passwordValid = current.techRegisterPassword.isNotBlank()
        _uiState.update {
            it.copy(
                isTechFullNameError = !nameValid,
                isTechRegisterEmailError = !emailValid,
                isWorkshopCodeError = !codeValid,
                isTechRegisterPasswordError = !passwordValid
            )
        }
        return nameValid && emailValid && codeValid && passwordValid
    }

    fun sendWorkshopVerificationCode() {
        // Trigger verification code sending logic
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
