package com.fixtech.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.fixtech.feature.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val sharedAuthState = MutableStateFlow(AuthUiState())
private val registeredUserNamesByEmail = mutableMapOf<String, String>()

class AuthViewModel : ViewModel() {

    val uiState: StateFlow<AuthUiState> = sharedAuthState.asStateFlow()

    // User Login
    fun onEmailChanged(email: String) {
        sharedAuthState.update {
            it.copy(
                email = email,
                isEmailError = if (email.isNotBlank()) false else it.isEmailError
            )
        }
    }

    fun onPasswordChanged(password: String) {
        sharedAuthState.update {
            it.copy(
                password = password,
                isPasswordError = if (password.isNotBlank()) false else it.isPasswordError
            )
        }
    }

    fun validateUserLogin(): Boolean {
        val current = sharedAuthState.value
        val emailValid = current.email.isNotBlank()
        val passwordValid = current.password.isNotBlank()
        sharedAuthState.update {
            it.copy(
                isEmailError = !emailValid,
                isPasswordError = !passwordValid
            )
        }
        if (emailValid && passwordValid) {
            val normalizedEmail = current.email.trim().lowercase()
            val savedName = registeredUserNamesByEmail[normalizedEmail] ?: ""
            ProfileViewModel.updateLoggedUser(fullName = savedName, email = current.email)
            sharedAuthState.update {
                it.copy(email = "", password = "", isEmailError = false, isPasswordError = false)
            }
        }
        return emailValid && passwordValid
    }

    // User Register
    fun onRegisterFullNameChanged(name: String) {
        sharedAuthState.update {
            it.copy(
                registerFullName = name,
                isRegisterFullNameError = if (name.isNotBlank()) false else it.isRegisterFullNameError
            )
        }
    }

    fun onRegisterEmailChanged(email: String) {
        sharedAuthState.update {
            it.copy(
                registerEmail = email,
                isRegisterEmailError = if (email.isNotBlank()) false else it.isRegisterEmailError
            )
        }
    }

    fun onRegisterPasswordChanged(password: String) {
        sharedAuthState.update {
            it.copy(
                registerPassword = password,
                isRegisterPasswordError = if (password.isNotBlank()) false else it.isRegisterPasswordError
            )
        }
    }

    fun validateUserRegister(): Boolean {
        val current = sharedAuthState.value
        val nameValid = current.registerFullName.isNotBlank()
        val emailValid = current.registerEmail.isNotBlank()
        val passwordValid = current.registerPassword.isNotBlank()
        sharedAuthState.update {
            it.copy(
                isRegisterFullNameError = !nameValid,
                isRegisterEmailError = !emailValid,
                isRegisterPasswordError = !passwordValid
            )
        }
        if (nameValid && emailValid && passwordValid) {
            val normalizedEmail = current.registerEmail.trim().lowercase()
            registeredUserNamesByEmail[normalizedEmail] = current.registerFullName.trim()
            ProfileViewModel.updateLoggedUser(
                fullName = current.registerFullName,
                email = current.registerEmail
            )
            sharedAuthState.update {
                it.copy(
                    registerFullName = "",
                    registerEmail = "",
                    registerPassword = "",
                    isRegisterFullNameError = false,
                    isRegisterEmailError = false,
                    isRegisterPasswordError = false
                )
            }
        }
        return nameValid && emailValid && passwordValid
    }

    // Tech Login
    fun onTechEmailChanged(email: String) {
        sharedAuthState.update {
            it.copy(
                techEmail = email,
                isTechEmailError = if (email.isNotBlank()) false else it.isTechEmailError
            )
        }
    }

    fun onTechPasswordChanged(password: String) {
        sharedAuthState.update {
            it.copy(
                techPassword = password,
                isTechPasswordError = if (password.isNotBlank()) false else it.isTechPasswordError
            )
        }
    }

    fun validateTechLogin(): Boolean {
        val current = sharedAuthState.value
        val emailValid = current.techEmail.isNotBlank()
        val passwordValid = current.techPassword.isNotBlank()
        sharedAuthState.update {
            it.copy(
                isTechEmailError = !emailValid,
                isTechPasswordError = !passwordValid
            )
        }
        if (emailValid && passwordValid) {
            ProfileViewModel.updateLoggedTech(fullName = "", email = current.techEmail)
            sharedAuthState.update {
                it.copy(techEmail = "", techPassword = "", isTechEmailError = false, isTechPasswordError = false)
            }
        }
        return emailValid && passwordValid
    }

    // Tech Register
    fun onTechFullNameChanged(name: String) {
        sharedAuthState.update {
            it.copy(
                techFullName = name,
                isTechFullNameError = if (name.isNotBlank()) false else it.isTechFullNameError
            )
        }
    }

    fun onTechRegisterEmailChanged(email: String) {
        sharedAuthState.update {
            it.copy(
                techRegisterEmail = email,
                isTechRegisterEmailError = if (email.isNotBlank()) false else it.isTechRegisterEmailError
            )
        }
    }

    fun onWorkshopCodeChanged(code: String) {
        sharedAuthState.update {
            it.copy(
                workshopCode = code,
                isWorkshopCodeError = if (code.isNotBlank()) false else it.isWorkshopCodeError
            )
        }
    }

    fun onTechRegisterPasswordChanged(password: String) {
        sharedAuthState.update {
            it.copy(
                techRegisterPassword = password,
                isTechRegisterPasswordError = if (password.isNotBlank()) false else it.isTechRegisterPasswordError
            )
        }
    }

    fun validateTechRegister(): Boolean {
        val current = sharedAuthState.value
        val nameValid = current.techFullName.isNotBlank()
        val emailValid = current.techRegisterEmail.isNotBlank()
        val codeValid = current.workshopCode.isNotBlank()
        val passwordValid = current.techRegisterPassword.isNotBlank()
        sharedAuthState.update {
            it.copy(
                isTechFullNameError = !nameValid,
                isTechRegisterEmailError = !emailValid,
                isWorkshopCodeError = !codeValid,
                isTechRegisterPasswordError = !passwordValid
            )
        }
        if (nameValid && emailValid && codeValid && passwordValid) {
            ProfileViewModel.updateLoggedTech(
                fullName = current.techFullName,
                email = current.techRegisterEmail
            )
            sharedAuthState.update {
                it.copy(
                    techFullName = "",
                    techRegisterEmail = "",
                    workshopCode = "",
                    techRegisterPassword = "",
                    isTechFullNameError = false,
                    isTechRegisterEmailError = false,
                    isWorkshopCodeError = false,
                    isTechRegisterPasswordError = false
                )
            }
        }
        return nameValid && emailValid && codeValid && passwordValid
    }

    fun sendWorkshopVerificationCode() {
        // Trigger verification code sending logic
    }

    fun resetState() {
        sharedAuthState.value = AuthUiState()
    }
}
