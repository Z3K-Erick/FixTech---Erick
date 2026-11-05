package com.fixtech.feature.profile.viewmodel

data class ProfileUiState(
    // Normal User profile data
    val userName: String = "Usuario",
    val userPlan: String = "Free",
    val userInitial: String = "U",
    val savedDevicesCount: Int = 0,
    val favoriteGuidesCount: Int = 0,
    val userNotificationsEnabled: Boolean = true,

    // Tech / Admin profile data
    val techName: String = "Técnico Especialista",
    val techInitial: String = "T",
    val techRole: String = "Rol",
    val techBadge: String = "Técnico Especialista",
    val techMyGuidesCount: Int = 0,
    val techInProgressCount: Int = 0,
    val techApprovedCount: Int = 0,
    val techNotificationsEnabled: Boolean = true,

    val isUploadingCertificate: Boolean = false
)
