package com.fixtech.data.remote.dto

import com.fixtech.core.common.Constants
import com.fixtech.data.local.entity.GuideEntity
import com.fixtech.data.local.entity.GuideStepEntity
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
    val confirmationText: String,
    val imageUrl: String = "",
    val resourceUrl: String? = null
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
        confirmationText = confirmationText,
        imageUrl = imageUrl,
        imageUri = imageUrl.ifBlank { null },
        resourceUrl = resourceUrl?.takeIf { it.isNotBlank() }
    )

    fun toEntity(guideId: String): GuideStepEntity = GuideStepEntity(
        id = "${guideId}_step_$stepNumber",
        guideId = guideId,
        stepNumber = stepNumber,
        title = title,
        description = description,
        hasAlert = hasAlert,
        alertType = alertType,
        alertMessage = alertMessage,
        requiresConfirmation = requiresConfirmation,
        confirmationText = confirmationText,
        alertTitle = alertType,
        imageUrl = imageUrl,
        imageUri = imageUrl.ifBlank { null },
        resourceUrl = resourceUrl?.takeIf { it.isNotBlank() }
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
    val imageUrl: String = "",
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
        imageUrl = imageUrl,
        steps = steps.map { it.toDomain(id) }
    )

    fun toEntity(): GuideEntity = GuideEntity(
        id = id,
        deviceId = deviceId,
        title = title,
        difficulty = difficulty,
        estimatedTime = estimatedTime,
        toolsList = toolsList.joinToString(Constants.TOOLS_SEPARATOR),
        precautions = precautions,
        technicianId = technicianId,
        technicianName = technicianName,
        category = category,
        modelSpec = modelSpec,
        imageUrl = imageUrl
    )

    fun toStepEntities(): List<GuideStepEntity> = steps.mapIndexed { index, step ->
        val resolvedStepNumber = if (step.stepNumber > 0) step.stepNumber else index + 1
        step.copy(stepNumber = resolvedStepNumber).toEntity(id)
    }
}

fun GuideStep.toDto(): GuideStepDto {
    val resolvedImg = imageUri?.takeIf { it.isNotBlank() } ?: imageUrl
    return GuideStepDto(
        stepNumber = stepNumber,
        title = title,
        description = description,
        hasAlert = hasAlert,
        alertType = alertType,
        alertMessage = alertMessage,
        requiresConfirmation = requiresConfirmation,
        confirmationText = confirmationText,
        imageUrl = resolvedImg,
        resourceUrl = resourceUrl?.takeIf { it.isNotBlank() }
    )
}

fun Guide.toDto(): GuideDto = GuideDto(
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
    imageUrl = imageUrl,
    steps = steps.map { it.toDto() }
)
