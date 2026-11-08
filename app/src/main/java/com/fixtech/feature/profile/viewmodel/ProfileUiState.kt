package com.fixtech.feature.profile.viewmodel

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
    val techName: String = "Erick",
    val techInitial: String = "E",
    val techRole: String = "Rol",
    val techBadge: String = "Técnico Especialista",
    val techMyGuidesCount: Int = 3,
    val techInProgressCount: Int = 1,
    val techApprovedCount: Int = 2,
    val techNotificationsEnabled: Boolean = true,

    val isUploadingCertificate: Boolean = false,
    val uploadedCertificateName: String? = null
)
