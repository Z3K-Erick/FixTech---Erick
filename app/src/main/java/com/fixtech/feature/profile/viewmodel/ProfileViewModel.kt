package com.fixtech.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleUserNotifications(enabled: Boolean) {
        _uiState.update { it.copy(userNotificationsEnabled = enabled) }
    }

    fun toggleTechNotifications(enabled: Boolean) {
        _uiState.update { it.copy(techNotificationsEnabled = enabled) }
    }

    fun uploadCertificate() {
        // Handle certificate upload action
    }
}
