package com.fixtech.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixtech.core.common.Constants
import com.fixtech.data.local.SessionManager
import com.fixtech.data.local.SessionState
import com.fixtech.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val sessionFlow: Flow<SessionState>?
        get() = sessionManager?.sessionFlow

    suspend fun restoreSessionFromDataStore(session: SessionState) {
        if (session.isLoggedIn) {
            authRepository.restoreSession(
                userId = session.userId,
                name = session.userName,
                role = session.role
            )
        }
    }

    fun onBiometricLoginSuccess(isTech: Boolean) {
        viewModelScope.launch {
            if (isTech) {
                val currentTech = authRepository.currentTech.value
                val emailOrId = currentTech.email.ifBlank { Constants.DEFAULT_TECH_ID }
                val tech = authRepository.loginTech(emailOrId, "biometric")
                sessionManager?.saveSession(
                    userId = if (tech.email.isNotBlank()) "tech_${tech.email.lowercase()}" else Constants.DEFAULT_TECH_ID,
                    name = tech.fullName,
                    role = "TECH"
                )
            } else {
                val currentUser = authRepository.currentUser.value
                val emailOrId = currentUser.email.ifBlank { currentUser.id.removePrefix("user_") }
                val user = authRepository.loginUser(emailOrId, "biometric")
                sessionManager?.saveSession(
                    userId = user.id,
                    name = user.fullName,
                    role = "USER"
                )
            }
        }
    }

    // User Login
    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                isEmailError = if (email.isNotBlank()) false else it.isEmailError,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isPasswordError = if (password.isNotBlank()) false else it.isPasswordError,
                errorMessage = null
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
        if (emailValid && passwordValid) {
            val emailToLogin = current.email
            val passwordToLogin = current.password
            viewModelScope.launch {
                val user = authRepository.loginUser(emailToLogin, passwordToLogin)
                sessionManager?.saveSession(
                    userId = user.id,
                    name = user.fullName,
                    role = "USER"
                )
            }
            _uiState.update {
                it.copy(email = "", password = "", isEmailError = false, isPasswordError = false, errorMessage = null)
            }
        }
        return emailValid && passwordValid
    }

    // User Register
    fun onRegisterFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                registerFullName = name,
                isRegisterFullNameError = if (name.isNotBlank()) false else it.isRegisterFullNameError,
                errorMessage = null
            )
        }
    }

    fun onRegisterEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                registerEmail = email,
                isRegisterEmailError = if (email.isNotBlank()) false else it.isRegisterEmailError,
                errorMessage = null
            )
        }
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                registerPassword = password,
                isRegisterPasswordError = if (password.isNotBlank()) false else it.isRegisterPasswordError,
                errorMessage = null
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
        if (nameValid && emailValid && passwordValid) {
            val fullName = current.registerFullName
            val email = current.registerEmail
            val password = current.registerPassword
            viewModelScope.launch {
                val user = authRepository.registerUser(fullName, email, password)
                sessionManager?.saveSession(
                    userId = user.id,
                    name = user.fullName,
                    role = "USER"
                )
            }
            _uiState.update {
                it.copy(
                    registerFullName = "",
                    registerEmail = "",
                    registerPassword = "",
                    isRegisterFullNameError = false,
                    isRegisterEmailError = false,
                    isRegisterPasswordError = false,
                    errorMessage = null
                )
            }
        }
        return nameValid && emailValid && passwordValid
    }

    // Tech Login
    fun onTechEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                techEmail = email,
                isTechEmailError = if (email.isNotBlank()) false else it.isTechEmailError,
                errorMessage = null
            )
        }
    }

    fun onTechPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                techPassword = password,
                isTechPasswordError = if (password.isNotBlank()) false else it.isTechPasswordError,
                errorMessage = null
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
        if (emailValid && passwordValid) {
            val email = current.techEmail
            val password = current.techPassword
            viewModelScope.launch {
                val tech = authRepository.loginTech(email, password)
                val techSessionId = if (tech.email.isNotBlank()) {
                    "tech_${tech.email.lowercase()}"
                } else {
                    Constants.DEFAULT_TECH_ID
                }
                sessionManager?.saveSession(
                    userId = techSessionId,
                    name = tech.fullName,
                    role = "TECH"
                )
            }
            _uiState.update {
                it.copy(techEmail = "", techPassword = "", isTechEmailError = false, isTechPasswordError = false, errorMessage = null)
            }
        }
        return emailValid && passwordValid
    }

    // Tech Register
    fun onTechFullNameChanged(name: String) {
        _uiState.update {
            it.copy(
                techFullName = name,
                isTechFullNameError = if (name.isNotBlank()) false else it.isTechFullNameError,
                errorMessage = null
            )
        }
    }

    fun onTechRegisterEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                techRegisterEmail = email,
                isTechRegisterEmailError = if (email.isNotBlank()) false else it.isTechRegisterEmailError,
                errorMessage = null
            )
        }
    }

    fun onWorkshopCodeChanged(code: String) {
        _uiState.update {
            it.copy(
                workshopCode = code,
                isWorkshopCodeError = if (code.isNotBlank()) false else it.isWorkshopCodeError,
                errorMessage = null
            )
        }
    }

    fun onTechRegisterPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                techRegisterPassword = password,
                isTechRegisterPasswordError = if (password.isNotBlank()) false else it.isTechRegisterPasswordError,
                errorMessage = null
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
        if (nameValid && emailValid && codeValid && passwordValid) {
            val fullName = current.techFullName
            val email = current.techRegisterEmail
            val code = current.workshopCode
            val password = current.techRegisterPassword
            viewModelScope.launch {
                val tech = authRepository.registerTech(fullName, email, code, password)
                val techSessionId = if (tech.email.isNotBlank()) {
                    "tech_${tech.email.lowercase()}"
                } else {
                    Constants.DEFAULT_TECH_ID
                }
                sessionManager?.saveSession(
                    userId = techSessionId,
                    name = tech.fullName,
                    role = "TECH"
                )
            }
            _uiState.update {
                it.copy(
                    techFullName = "",
                    techRegisterEmail = "",
                    workshopCode = "",
                    techRegisterPassword = "",
                    isTechFullNameError = false,
                    isTechRegisterEmailError = false,
                    isWorkshopCodeError = false,
                    isTechRegisterPasswordError = false,
                    errorMessage = null
                )
            }
        }
        return nameValid && emailValid && codeValid && passwordValid
    }

    fun sendWorkshopVerificationCode() {
        // Trigger verification code sending logic
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
