package com.fixtech.data.remote.dto

import com.fixtech.domain.model.Guide
import com.fixtech.domain.model.GuideStep

data class GuideStepDto(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val hasAlert: Boolean,
    val alertType: String,
    val alertMessage: String,
    val requiresConfirmation: Boolean,
    val confirmationText: String
) {
    fun toDomain(guideId: String): GuideStep = GuideStep(
        id = "${guideId}_step_$stepNumber",
        guideId = guideId,
        stepNumber = stepNumber,
        title = title,
        description = description,
        hasAlert = hasAlert,
        alertType = alertType,
        alertTitle = alertType,
        alertMessage = alertMessage,
        requiresConfirmation = requiresConfirmation,
        confirmationText = confirmationText
    )
}

data class GuideDto(
    val id: String,
    val deviceId: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val estimatedTime: String,
    val toolsList: List<String>,
    val precautions: String,
    val technicianId: String,
    val technicianName: String,
    val modelSpec: String = "",
    val steps: List<GuideStepDto> = emptyList()
) {
    fun toDomain(): Guide = Guide(
        id = id,
        deviceId = deviceId,
        title = title,
        category = category,
        difficulty = difficulty,
        estimatedTime = estimatedTime,
        toolsList = toolsList,
        precautions = precautions,
        technicianId = technicianId,
        technicianName = technicianName,
        modelSpec = modelSpec,
        steps = steps.map { it.toDomain(id) }
    )
}
