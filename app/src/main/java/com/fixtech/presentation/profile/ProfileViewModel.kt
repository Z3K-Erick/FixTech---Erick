package com.fixtech.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixtech.core.common.Constants
import com.fixtech.data.local.SessionManager
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.DeviceRepository
import com.fixtech.domain.repository.GuideRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
    private val guideRepository: GuideRepository,
    private val sessionManager: SessionManager? = null
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
                        userAvatarUri = user.avatarUri,
                        userNotificationsEnabled = user.notificationsEnabled
                    )
                }
            }
        }

        // 2. Técnico actual (Nombre real, correo real, avatar, etiqueta "Técnico Administrador")
        viewModelScope.launch {
            authRepository.currentTech.collect { tech ->
                val resolvedName = tech.fullName.ifBlank { Constants.DEFAULT_TECH_NAME }
                val resolvedEmail = tech.email.ifBlank { "carlos.tech@fixtech.com" }
                val initial = resolvedName.firstOrNull()?.uppercase() ?: "C"
                _uiState.update {
                    it.copy(
                        techName = resolvedName,
                        techEmail = resolvedEmail,
                        techInitial = initial,
                        techAvatarUri = tech.avatarUri,
                        techBadge = "Técnico Administrador",
                        techNotificationsEnabled = tech.notificationsEnabled
                    )
                }
            }
        }

        // 3. Contador de Equipos Guardados desde Room (Usuario Normal)
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

        // 4. Contador de Guías Favoritas desde Room (Usuario Normal)
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

        // 5. Contador real de "Mis Equipos" del técnico desde Room
        viewModelScope.launch {
            deviceRepository.observeTechDevicesCount(Constants.DEFAULT_TECH_ID)
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { devicesCount ->
                    _uiState.update { it.copy(techMyDevicesCount = devicesCount) }
                }
        }

        // 6. Contador real de "Mis Guías" del técnico desde Room
        viewModelScope.launch {
            guideRepository.observeTechGuidesCount(Constants.DEFAULT_TECH_ID)
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { guidesCount ->
                    _uiState.update { it.copy(techMyGuidesCount = guidesCount) }
                }
        }

        // 7. Lista y contador real de "Certificados" del técnico desde Room
        viewModelScope.launch {
            authRepository.observeCertificatesForTech(Constants.DEFAULT_TECH_ID)
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { certs ->
                    _uiState.update {
                        it.copy(
                            techCertificates = certs,
                            techCertificatesCount = certs.size,
                            uploadedCertificateName = certs.lastOrNull()?.fileName
                        )
                    }
                }
        }
    }

    fun observeCertificatesForTech(technicianId: String): Flow<List<Certificate>> {
        val resolvedId = technicianId.ifBlank { Constants.DEFAULT_TECH_ID }
        return authRepository.observeCertificatesForTech(resolvedId)
    }

    fun updateUserAvatar(avatarUri: String) {
        viewModelScope.launch {
            authRepository.updateUserAvatar(avatarUri)
        }
    }

    fun updateTechAvatar(avatarUri: String) {
        viewModelScope.launch {
            authRepository.updateTechAvatar(avatarUri)
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

    fun dismissCertNotice() {
        _uiState.update { it.copy(isCertNoticeDismissed = true) }
    }

    fun addTechCertificate(
        title: String,
        institution: String,
        fileName: String,
        localFilePath: String,
        mimeType: String,
        onSuccess: () -> Unit = {}
    ) {
        val cleanTitle = title.trim()
        val cleanInstitution = institution.trim()
        val cleanFileName = fileName.trim().ifEmpty { "certificado_tecnico.pdf" }
        if (cleanTitle.isEmpty()) return

        _uiState.update { it.copy(isUploadingCertificate = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.addCertificate(
                    technicianId = Constants.DEFAULT_TECH_ID,
                    title = cleanTitle,
                    institution = cleanInstitution,
                    fileName = cleanFileName,
                    localFilePath = localFilePath,
                    mimeType = mimeType
                )
                _uiState.update {
                    it.copy(
                        isUploadingCertificate = false,
                        uploadedCertificateName = cleanFileName,
                        errorMessage = null
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingCertificate = false,
                        errorMessage = "Error al guardar el certificado en Room"
                    )
                }
            }
        }
    }

    fun deleteTechCertificate(certificateId: String) {
        viewModelScope.launch {
            try {
                authRepository.deleteCertificate(certificateId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al eliminar el certificado") }
            }
        }
    }

    fun uploadCertificate(fileName: String = "certificado_tecnico.pdf") {
        addTechCertificate(
            title = "Certificado Técnico Especialista",
            institution = "FixTech Academy",
            fileName = fileName,
            localFilePath = "",
            mimeType = "application/pdf"
        )
    }

    fun clearSession() {
        viewModelScope.launch {
            sessionManager?.clearSession()
            authRepository.logoutUser()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
