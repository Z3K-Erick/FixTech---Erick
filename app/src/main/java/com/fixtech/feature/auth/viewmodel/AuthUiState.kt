package com.fixtech.feature.auth.viewmodel

data class AuthUiState(
    // User login
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,

    // User register
    val registerFullName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val isRegisterFullNameError: Boolean = false,
    val isRegisterEmailError: Boolean = false,
    val isRegisterPasswordError: Boolean = false,

    // Tech login
    val techEmail: String = "",
    val techPassword: String = "",
    val isTechEmailError: Boolean = false,
    val isTechPasswordError: Boolean = false,

    // Tech register
    val techFullName: String = "",
    val techRegisterEmail: String = "",
    val workshopCode: String = "",
    val techRegisterPassword: String = "",
    val isTechFullNameError: Boolean = false,
    val isTechRegisterEmailError: Boolean = false,
    val isWorkshopCodeError: Boolean = false,
    val isTechRegisterPasswordError: Boolean = false,

    // UI flags
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
