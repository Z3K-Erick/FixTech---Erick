package com.fixtech.feature.auth.viewmodel

data class AuthUiState(
    // User login
    val email: String = "",
    val password: String = "",

    // User register
    val registerFullName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",

    // Tech login
    val techEmail: String = "",
    val techPassword: String = "",

    // Tech register
    val techFullName: String = "",
    val techRegisterEmail: String = "",
    val workshopCode: String = "",
    val techRegisterPassword: String = "",

    // UI flags
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
