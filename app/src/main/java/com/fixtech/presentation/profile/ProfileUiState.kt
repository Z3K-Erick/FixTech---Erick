package com.fixtech.presentation.profile

import com.fixtech.domain.model.Certificate

data class ProfileUiState(
    // Normal User profile data
    val userName: String = "Usuario",
    val userEmail: String = "",
    val userPlan: String = "Free",
    val userInitial: String = "U",
    val savedDevicesCount: Int = 0,
    val favoriteGuidesCount: Int = 0,
    val userNotificationsEnabled: Boolean = true,

    // Tech / Admin profile data
    val techName: String = "Carlos M.",
    val techEmail: String = "carlos.tech@fixtech.com",
    val techInitial: String = "C",
    val techRole: String = "Técnico Administrador",
    val techBadge: String = "Técnico Administrador",
    val techMyDevicesCount: Int = 0,
    val techMyGuidesCount: Int = 0,
    val techCertificatesCount: Int = 0,
    val techCertificates: List<Certificate> = emptyList(),
    val isCertNoticeDismissed: Boolean = false,
    val techNotificationsEnabled: Boolean = true,

    val isUploadingCertificate: Boolean = false,
    val uploadedCertificateName: String? = null,

    // UI flags obligatorios
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
