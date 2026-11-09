package com.fixtech.data.local.entity

import com.fixtech.core.common.Constants
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.Device
import com.fixtech.domain.model.Guide
import com.fixtech.domain.model.GuideStep
import com.fixtech.domain.model.User

fun DeviceEntity.toDomain(
    isBookmarked: Boolean = false,
    guidesCount: Int = 0
): Device = Device(
    id = id,
    name = name,
    model = model,
    type = type,
    technicianId = technicianId,
    technicianName = technicianName,
    imageUrl = imageUrl,
    ram = ram,
    storage = storage,
    processor = processor,
    isBookmarked = isBookmarked,
    guidesCount = guidesCount
)

fun DeviceWithBookmarkRow.toDomain(): Device = device.toDomain(
    isBookmarked = isBookmarked,
    guidesCount = guidesCount
)

fun Device.toEntity(): DeviceEntity = DeviceEntity(
    id = id,
    name = name,
    model = model,
    type = type,
    technicianId = technicianId,
    technicianName = technicianName,
    imageUrl = imageUrl,
    ram = ram,
    storage = storage,
    processor = processor
)

fun GuideStepEntity.toDomain(): GuideStep {
    val resolvedImage = imageUri?.takeIf { it.isNotBlank() } ?: imageUrl
    return GuideStep(
        id = id,
        guideId = guideId,
        stepNumber = stepNumber,
        title = title,
        description = description,
        hasAlert = hasAlert,
        alertType = alertType,
        alertTitle = alertTitle,
        alertMessage = alertMessage,
        requiresConfirmation = requiresConfirmation,
        confirmationText = confirmationText,
        imageUrl = resolvedImage,
        imageUri = resolvedImage.ifBlank { null },
        resourceUrl = resourceUrl?.takeIf { it.isNotBlank() }
    )
}

fun GuideStep.toEntity(parentGuideId: String): GuideStepEntity {
    val resolvedImage = imageUri?.takeIf { it.isNotBlank() } ?: imageUrl
    return GuideStepEntity(
        id = id.ifBlank { "${parentGuideId}_step_$stepNumber" },
        guideId = parentGuideId,
        stepNumber = stepNumber,
        title = title,
        description = description,
        hasAlert = hasAlert,
        alertType = alertType,
        alertMessage = alertMessage,
        requiresConfirmation = requiresConfirmation,
        confirmationText = confirmationText,
        alertTitle = alertTitle,
        imageUrl = resolvedImage,
        imageUri = resolvedImage.ifBlank { null },
        resourceUrl = resourceUrl?.takeIf { it.isNotBlank() }
    )
}

fun GuideEntity.toDomain(
    steps: List<GuideStep> = emptyList(),
    isFavorite: Boolean = false,
    pinnedAt: Long = 0L
): Guide {
    val parsedTools = if (toolsList.contains(Constants.TOOLS_SEPARATOR)) {
        toolsList.split(Constants.TOOLS_SEPARATOR).map { it.trim() }.filter { it.isNotEmpty() }
    } else {
        toolsList.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    return Guide(
        id = id,
        deviceId = deviceId,
        title = title,
        category = category,
        difficulty = difficulty,
        estimatedTime = estimatedTime,
        toolsList = parsedTools,
        precautions = precautions,
        technicianId = technicianId,
        technicianName = technicianName,
        modelSpec = modelSpec,
        imageUrl = imageUrl,
        steps = steps.sortedBy { it.stepNumber },
        isFavorite = isFavorite,
        pinnedAt = pinnedAt
    )
}

fun GuideWithFavoriteRow.toDomain(steps: List<GuideStep>): Guide = guide.toDomain(
    steps = steps,
    isFavorite = isFavorite,
    pinnedAt = pinnedAt ?: 0L
)

fun Guide.toEntity(): GuideEntity = GuideEntity(
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

fun UserEntity.toDomain(): User = User(
    id = id,
    fullName = fullName,
    email = email,
    role = role,
    workshopCode = workshopCode,
    notificationsEnabled = notificationsEnabled,
    avatarUri = avatarUri
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    fullName = fullName,
    email = email,
    role = role,
    workshopCode = workshopCode,
    notificationsEnabled = notificationsEnabled,
    avatarUri = avatarUri
)

fun CertificateEntity.toDomain(): Certificate = Certificate(
    id = id,
    technicianId = technicianId,
    title = title,
    institution = institution,
    fileName = fileName,
    localFilePath = localFilePath,
    mimeType = mimeType
)

fun Certificate.toEntity(): CertificateEntity = CertificateEntity(
    id = id,
    technicianId = technicianId,
    title = title,
    institution = institution,
    fileName = fileName,
    localFilePath = localFilePath,
    mimeType = mimeType
)
