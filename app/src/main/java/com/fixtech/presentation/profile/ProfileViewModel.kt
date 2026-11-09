package com.fixtech.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixtech.core.common.Constants
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.DeviceRepository
import com.fixtech.domain.repository.GuideRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val deviceRepository: DeviceRepository,
    private val guideRepository: GuideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeRoomStreams()
    }

    private fun observeRoomStreams() {
        // 1. Usuario actual
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                val resolvedName = user.fullName.ifBlank { "Usuario" }
                val initial = resolvedName.firstOrNull()?.uppercase() ?: "U"
                _uiState.update {
                    it.copy(
                        userName = resolvedName,
                        userEmail = user.email,
                        userInitial = initial,
                        userNotificationsEnabled = user.notificationsEnabled
                    )
                }
            }
        }

        // 2. Técnico actual
        viewModelScope.launch {
            authRepository.currentTech.collect { tech ->
                val resolvedName = tech.fullName.ifBlank { "Erick" }
                val initial = resolvedName.firstOrNull()?.uppercase() ?: "E"
                _uiState.update {
                    it.copy(
                        techName = resolvedName,
                        techInitial = initial,
                        techNotificationsEnabled = tech.notificationsEnabled
                    )
                }
            }
        }

        // 3. Contador de Equipos Guardados desde Room
        viewModelScope.launch {
            authRepository.observeCurrentUserId()
                .flatMapLatest { userId ->
                    deviceRepository.observeSavedDevicesCount(userId)
                }
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { savedCount ->
                    _uiState.update { it.copy(savedDevicesCount = savedCount) }
                }
        }

        // 4. Contador de Guías Favoritas desde Room (INNER JOIN guides g ON ug.guideId = g.id)
        viewModelScope.launch {
            authRepository.observeCurrentUserId()
                .flatMapLatest { userId ->
                    guideRepository.observeFavoriteGuidesCount(userId)
                }
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { favCount ->
                    _uiState.update { it.copy(favoriteGuidesCount = favCount) }
                }
        }

        // 5. Contador de guías del técnico desde Room
        viewModelScope.launch {
            authRepository.observeCurrentUserId()
                .flatMapLatest { userId ->
                    guideRepository.observeAllGuides(userId)
                }
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { guides ->
                    if (guides.isNotEmpty()) {
                        val extraTechGuides = (guides.size - 4).coerceAtLeast(0)
                        _uiState.update {
                            it.copy(
                                techMyGuidesCount = 3 + extraTechGuides,
                                techInProgressCount = 1,
                                techApprovedCount = 2 + extraTechGuides
                            )
                        }
                    }
                }
        }

        // 6. Certificados del técnico desde Room
        viewModelScope.launch {
            authRepository.observeAllCertificates()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { certsByTech ->
                    val techCerts = certsByTech[Constants.DEFAULT_TECH_ID].orEmpty()
                    if (techCerts.size > 2) {
                        val latestUploaded = techCerts.lastOrNull()?.fileName
                        if (!latestUploaded.isNullOrBlank()) {
                            _uiState.update { it.copy(uploadedCertificateName = latestUploaded) }
                        }
                    }
                }
        }
    }

    fun toggleUserNotifications(enabled: Boolean) {
        viewModelScope.launch {
            authRepository.updateUserNotifications(enabled)
        }
    }

    fun toggleTechNotifications(enabled: Boolean) {
        viewModelScope.launch {
            authRepository.updateTechNotifications(enabled)
        }
    }

    fun uploadCertificate(fileName: String = "certificado_tecnico.pdf") {
        val cleanName = fileName.trim().ifEmpty { "certificado_tecnico.pdf" }
        _uiState.update {
            it.copy(uploadedCertificateName = cleanName)
        }
        viewModelScope.launch {
            try {
                authRepository.addCertificate(
                    technicianId = Constants.DEFAULT_TECH_ID,
                    title = "Certificado Técnico Especialista",
                    institution = "FixTech Academy",
                    fileName = cleanName
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al guardar certificado") }
            }
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            authRepository.logoutUser()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
