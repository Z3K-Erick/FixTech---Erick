package com.fixtech.domain.model

data class GuideStep(
    val id: String = "",
    val guideId: String = "",
    val stepNumber: Int,
    val title: String,
    val description: String,
    val hasAlert: Boolean = true,
    val alertType: String = "Precaución",
    val alertTitle: String = "Precaución",
    val alertMessage: String = "",
    val requiresConfirmation: Boolean = false,
    val confirmationText: String = "",
    val imageUrl: String = ""
) {
    val instruction: String get() = description
    val alertLevel: String get() = alertType
    val alertDescription: String get() = alertMessage
    val imageUri: String get() = imageUrl
}
