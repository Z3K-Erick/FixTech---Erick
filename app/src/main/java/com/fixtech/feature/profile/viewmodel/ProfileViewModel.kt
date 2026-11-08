package com.fixtech.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.fixtech.feature.guide.viewmodel.GuideViewModel
import com.fixtech.feature.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val sharedProfileState = MutableStateFlow(ProfileUiState())
private val notificationsByUser = mutableMapOf<String, Boolean>()
private var activeProfileUserKey: String = "default_user"

class ProfileViewModel : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = sharedProfileState.asStateFlow()

    fun toggleUserNotifications(enabled: Boolean) {
        notificationsByUser[activeProfileUserKey] = enabled
        sharedProfileState.update { it.copy(userNotificationsEnabled = enabled) }
    }

    fun toggleTechNotifications(enabled: Boolean) {
        sharedProfileState.update { it.copy(techNotificationsEnabled = enabled) }
    }

    fun uploadCertificate() {
        sharedProfileState.update {
            it.copy(
                uploadedCertificateName = "certificado_tecnico.pdf",
                techApprovedCount = it.techApprovedCount
            )
        }
        HomeViewModel.addTechCertificate(
            title = "Certificado Técnico Especialista",
            institution = "FixTech Academy",
            fileName = "certificado_tecnico.pdf"
        )
    }

    fun clearSession() {
        sharedProfileState.update {
            it.copy(
                userName = "Usuario",
                userEmail = "",
                userInitial = "U",
                savedDevicesCount = 0,
                favoriteGuidesCount = 0,
                userNotificationsEnabled = true
            )
        }
        HomeViewModel.clearUserSession()
        GuideViewModel.clearUserSession()
    }

    companion object {
        fun updateLoggedUser(fullName: String, email: String) {
            val trimmedEmail = email.trim()
            val userKey = trimmedEmail.lowercase().ifEmpty { "default_user" }
            activeProfileUserKey = userKey
            val resolvedName = when {
                fullName.trim().isNotEmpty() -> fullName.trim()
                trimmedEmail.isNotEmpty() -> trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                else -> "Usuario"
            }
            val initial = resolvedName.firstOrNull()?.uppercase() ?: "U"
            val notifPref = notificationsByUser[userKey] ?: true
            sharedProfileState.update {
                it.copy(
                    userName = resolvedName,
                    userEmail = trimmedEmail,
                    userInitial = initial,
                    userNotificationsEnabled = notifPref
                )
            }
            HomeViewModel.switchUserSession(trimmedEmail)
            GuideViewModel.switchUserSession(trimmedEmail)
        }

        fun updateLoggedTech(fullName: String, email: String) {
            val resolvedName = when {
                fullName.trim().isNotEmpty() -> fullName.trim()
                email.trim().isNotEmpty() -> email.trim().substringBefore("@").replaceFirstChar { it.uppercase() }
                else -> "Carlos M."
            }
            val initial = resolvedName.firstOrNull()?.uppercase() ?: "C"
            sharedProfileState.update {
                it.copy(
                    techName = resolvedName,
                    techInitial = initial
                )
            }
        }

        fun getActiveTechName(): String {
            return sharedProfileState.value.techName.ifBlank { "Carlos M." }
        }

        fun updateSavedDevicesCount(count: Int) {
            sharedProfileState.update { it.copy(savedDevicesCount = count) }
        }

        fun updateFavoriteGuidesCount(count: Int) {
            sharedProfileState.update { it.copy(favoriteGuidesCount = count) }
        }

        fun incrementTechGuidesCount() {
            sharedProfileState.update {
                it.copy(
                    techMyGuidesCount = it.techMyGuidesCount + 1,
                    techInProgressCount = it.techInProgressCount + 1
                )
            }
        }
    }
}
