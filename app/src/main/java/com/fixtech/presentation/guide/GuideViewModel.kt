package com.fixtech.presentation.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixtech.core.common.Constants
import com.fixtech.domain.model.Device
import com.fixtech.domain.model.Guide
import com.fixtech.domain.model.GuideStep
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.GuideRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GuideViewModel(
    private val guideRepository: GuideRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _activeDeviceId = MutableStateFlow("dev_1")
    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    init {
        observeRoomStreams()
    }

    private fun observeRoomStreams() {
        // 1. Observar todas las guías
        viewModelScope.launch {
            authRepository.observeCurrentUserId()
                .flatMapLatest { userId ->
                    guideRepository.observeAllGuides(userId)
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { allGuides ->
                    if (allGuides.isNotEmpty()) {
                        _uiState.update { current ->
                            val selected = allGuides.find { it.id == current.selectedGuideId } ?: allGuides.first()
                            val currentStepIndex = (current.currentStep - 1).coerceIn(0, (selected.steps.size - 1).coerceAtLeast(0))
                            val step = selected.steps.getOrNull(currentStepIndex)
                            val total = selected.steps.size.coerceAtLeast(1)
                            current.copy(
                                guides = allGuides,
                                title = selected.title,
                                categoryBadge = selected.category,
                                estimatedTimeLabel = selected.estimatedTime,
                                difficultyLabel = selected.difficulty,
                                authorId = selected.authorId,
                                authorLabel = selected.author,
                                description = selected.description,
                                tools = selected.tools,
                                imageUrl = selected.imageUrl,
                                totalSteps = total,
                                stepTitle = step?.title ?: current.stepTitle,
                                stepInstruction = step?.instruction ?: current.stepInstruction,
                                stepImageUrl = step?.imageUrl ?: "",
                                stepHasAlert = step?.hasAlert ?: current.stepHasAlert,
                                stepAlertLevel = step?.alertType ?: current.stepAlertLevel,
                                cautionTitle = step?.alertTitle ?: current.cautionTitle,
                                cautionDescription = step?.alertMessage ?: current.cautionDescription,
                                stepRequiresConfirmation = step?.requiresConfirmation ?: current.stepRequiresConfirmation,
                                stepConfirmationText = step?.confirmationText ?: current.stepConfirmationText,
                                isLoading = false
                            )
                        }
                    }
                }
        }

        // 2. Observar guías por dispositivo con ordenamiento tipo Playlist (CASE WHEN fav.guideId IS NOT NULL ... pinnedAt DESC)
        viewModelScope.launch {
            combine(
                _activeDeviceId,
                authRepository.observeCurrentUserId()
            ) { deviceId, userId ->
                deviceId to userId
            }.flatMapLatest { (deviceId, userId) ->
                guideRepository.observeGuidesForDevice(deviceId, userId)
            }.catch { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }.collect { deviceGuides ->
                _uiState.update { current ->
                    val selectedInDevice = deviceGuides.find { it.id == current.selectedGuideId }
                    current.copy(
                        deviceGuides = deviceGuides,
                        activeDeviceId = _activeDeviceId.value,
                        isCurrentGuideFavorite = selectedInDevice?.isFavorite ?: current.isCurrentGuideFavorite
                    )
                }
            }
        }
    }

    fun setActiveDevice(deviceId: String) {
        if (deviceId.isNotBlank()) {
            _activeDeviceId.value = deviceId
            _uiState.update { it.copy(activeDeviceId = deviceId) }
        }
    }

    fun selectGuide(guideId: String, deviceId: String? = null) {
        if (!deviceId.isNullOrBlank()) {
            _activeDeviceId.value = deviceId
        }
        _uiState.update { current ->
            val sourceList = current.deviceGuides.ifEmpty { current.guides }
            val guide = sourceList.find { it.id == guideId }
                ?: current.guides.find { it.id == guideId }
                ?: current.guides.first()
            val firstStep = guide.steps.firstOrNull() ?: GuideStep(
                stepNumber = 1,
                title = "Paso 1",
                description = "Sigue las instrucciones indicadas.",
                alertType = "Precaución",
                alertTitle = "Precaución",
                alertMessage = "Procede con cuidado."
            )
            val total = guide.steps.size.coerceAtLeast(1)
            val resolvedDeviceId = deviceId?.takeIf { it.isNotBlank() } ?: current.activeDeviceId.ifBlank { guide.deviceId }
            val isFavInDevice = current.deviceGuides.find { it.id == guide.id }?.isFavorite ?: guide.isFavorite
            current.copy(
                activeDeviceId = resolvedDeviceId,
                selectedGuideId = guide.id,
                title = guide.title,
                categoryBadge = guide.category,
                estimatedTimeLabel = guide.estimatedTime,
                difficultyLabel = guide.difficulty,
                authorId = guide.authorId,
                authorLabel = guide.author,
                description = guide.description,
                tools = guide.tools,
                imageUrl = guide.imageUrl,
                isCurrentGuideFavorite = isFavInDevice,
                currentStep = 1,
                totalSteps = total,
                stepTitle = firstStep.title,
                stepInstruction = firstStep.instruction,
                stepImageUrl = firstStep.imageUrl,
                stepHasAlert = firstStep.hasAlert,
                stepAlertLevel = firstStep.alertType,
                cautionTitle = firstStep.alertTitle,
                cautionDescription = firstStep.alertMessage,
                stepRequiresConfirmation = firstStep.requiresConfirmation,
                stepConfirmationText = firstStep.confirmationText,
                isStepAlertConfirmed = false,
                progress = 1f / total.toFloat()
            )
        }
    }

    fun toggleFavoriteGuide(
        guideId: String = _uiState.value.selectedGuideId,
        deviceId: String = _uiState.value.activeDeviceId
    ) {
        val targetDeviceId = deviceId.ifBlank {
            _uiState.value.guides.find { it.id == guideId }?.deviceId ?: "dev_1"
        }
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUser.value.id.ifBlank { Constants.DEFAULT_USER_ID }
                guideRepository.toggleFavoriteGuide(
                    userId = userId,
                    deviceId = targetDeviceId,
                    guideId = guideId
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al actualizar guía favorita") }
            }
        }
    }

    fun toggleStepAlertConfirmation() {
        _uiState.update { current ->
            current.copy(isStepAlertConfirmed = !current.isStepAlertConfirmed)
        }
    }

    fun setStepAlertConfirmation(confirmed: Boolean) {
        _uiState.update { current ->
            current.copy(isStepAlertConfirmed = confirmed)
        }
    }

    fun nextStep() {
        _uiState.update { current ->
            val guide = current.guides.find { it.id == current.selectedGuideId } ?: current.guides.first()
            if (current.currentStep < current.totalSteps) {
                val next = current.currentStep + 1
                val stepObj = guide.steps.getOrNull(next - 1)
                current.copy(
                    currentStep = next,
                    stepTitle = stepObj?.title ?: current.stepTitle,
                    stepInstruction = stepObj?.instruction ?: current.stepInstruction,
                    stepImageUrl = stepObj?.imageUrl ?: "",
                    stepHasAlert = stepObj?.hasAlert ?: false,
                    stepAlertLevel = stepObj?.alertType ?: "Precaución",
                    cautionTitle = stepObj?.alertTitle ?: current.cautionTitle,
                    cautionDescription = stepObj?.alertMessage ?: current.cautionDescription,
                    stepRequiresConfirmation = stepObj?.requiresConfirmation ?: false,
                    stepConfirmationText = stepObj?.confirmationText ?: "",
                    isStepAlertConfirmed = false,
                    progress = next.toFloat() / current.totalSteps.toFloat()
                )
            } else {
                current
            }
        }
    }

    fun previousStep() {
        _uiState.update { current ->
            val guide = current.guides.find { it.id == current.selectedGuideId } ?: current.guides.first()
            if (current.currentStep > 1) {
                val prev = current.currentStep - 1
                val stepObj = guide.steps.getOrNull(prev - 1)
                current.copy(
                    currentStep = prev,
                    stepTitle = stepObj?.title ?: current.stepTitle,
                    stepInstruction = stepObj?.instruction ?: current.stepInstruction,
                    stepImageUrl = stepObj?.imageUrl ?: "",
                    stepHasAlert = stepObj?.hasAlert ?: false,
                    stepAlertLevel = stepObj?.alertType ?: "Precaución",
                    cautionTitle = stepObj?.alertTitle ?: current.cautionTitle,
                    cautionDescription = stepObj?.alertMessage ?: current.cautionDescription,
                    stepRequiresConfirmation = stepObj?.requiresConfirmation ?: false,
                    stepConfirmationText = stepObj?.confirmationText ?: "",
                    isStepAlertConfirmed = false,
                    progress = prev.toFloat() / current.totalSteps.toFloat()
                )
            } else {
                current
            }
        }
    }

    fun resetSteps() {
        selectGuide(_uiState.value.selectedGuideId, _uiState.value.activeDeviceId)
    }

    // Create Guide Form handlers
    fun onFormGuideTitleChanged(title: String) {
        _uiState.update {
            it.copy(
                formGuideTitle = title,
                errorMessage = if (title.isNotBlank()) null else it.errorMessage
            )
        }
    }

    fun onFormDeviceSelected(deviceId: String, category: String, modelSpec: String) {
        _uiState.update {
            it.copy(
                formSelectedDeviceId = deviceId,
                formSelectedCategory = category,
                formModelSpec = modelSpec
            )
        }
    }

    fun onFormCategorySelected(category: String) {
        _uiState.update { it.copy(formSelectedCategory = category) }
    }

    fun onFormDifficultySelected(difficulty: String) {
        _uiState.update { it.copy(formSelectedDifficulty = difficulty) }
    }

    fun onFormEstimatedTimeChanged(time: String) {
        _uiState.update { it.copy(formEstimatedTime = time) }
    }

    fun onFormModelSpecChanged(model: String) {
        _uiState.update { it.copy(formModelSpec = model) }
    }

    fun onFormCoverImageSelected(fileName: String, imageUri: String) {
        _uiState.update {
            it.copy(
                formCoverImageFileName = fileName,
                formCoverImageUri = imageUri
            )
        }
    }

    fun onFormToolInputChanged(input: String) {
        _uiState.update { it.copy(formToolInput = input) }
    }

    fun addFormTool() {
        _uiState.update { current ->
            val trimmed = current.formToolInput.trim()
            if (trimmed.isNotEmpty()) {
                current.copy(
                    formToolsList = current.formToolsList + trimmed,
                    formToolInput = ""
                )
            } else {
                current
            }
        }
    }

    fun removeFormTool(index: Int) {
        _uiState.update { current ->
            if (index in current.formToolsList.indices) {
                current.copy(
                    formToolsList = current.formToolsList.filterIndexed { i, _ -> i != index }
                )
            } else {
                current
            }
        }
    }

    fun addStepBlock() {
        _uiState.update { current ->
            val updatedList = current.formSteps + StepFormState()
            current.copy(
                formStepCount = updatedList.size,
                formSteps = updatedList
            )
        }
    }

    fun removeStepBlock(index: Int) {
        _uiState.update { current ->
            if (current.formSteps.size > 1 && index in current.formSteps.indices) {
                val updatedList = current.formSteps.filterIndexed { i, _ -> i != index }
                current.copy(
                    formStepCount = updatedList.size,
                    formSteps = updatedList
                )
            } else {
                current
            }
        }
    }

    fun onStepTitleChanged(index: Int, title: String) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(title = title)
                current.copy(
                    formSteps = updated,
                    errorMessage = if (title.isNotBlank()) null else current.errorMessage
                )
            } else {
                current
            }
        }
    }

    fun onStepDescriptionChanged(index: Int, description: String) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(description = description)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepImageSelected(index: Int, fileName: String, imageUri: String) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(
                    imageFileName = fileName,
                    imageUri = imageUri
                )
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepCautionToggled(index: Int, enabled: Boolean) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(includeCautionAlert = enabled)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepAlertLevelSelected(index: Int, level: String) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(selectedAlertLevel = level)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepAlertTextChanged(index: Int, text: String) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(alertText = text)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepRequiresConfirmationToggled(index: Int, required: Boolean) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                val defaultConfirm = if (required && updated[index].confirmationText.isBlank()) {
                    "Batería desconectada"
                } else {
                    updated[index].confirmationText
                }
                updated[index] = updated[index].copy(
                    requiresConfirmation = required,
                    confirmationText = defaultConfirm
                )
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepConfirmationTextChanged(index: Int, text: String) {
        _uiState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(confirmationText = text)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun publishGuide(availableDevices: List<Device> = emptyList()): Boolean {
        val current = _uiState.value
        val title = current.formGuideTitle.trim()
        val hasInvalidStep = current.formSteps.isEmpty() || current.formSteps.any { it.title.trim().isEmpty() }

        if (title.isEmpty() || hasInvalidStep) {
            _uiState.update {
                it.copy(
                    errorMessage = "Por favor, completa el título de la guía y el título de cada paso antes de publicar."
                )
            }
            return false
        }

        val category = if (current.formSelectedCategory.contains("PC", ignoreCase = true)) "PC" else "Laptop"
        val difficultyNormalized = when (current.formSelectedDifficulty) {
            "Básico", "Bajo", "Fácil" -> "Fácil"
            "Avanzado", "Alto", "Difícil" -> "Difícil"
            else -> "Media"
        }
        val newGuideId = "guide_${System.currentTimeMillis()}"
        val steps = current.formSteps.mapIndexed { idx, s ->
            val confirmEnabled = s.includeCautionAlert && s.requiresConfirmation
            val confirmLabel = if (confirmEnabled) {
                s.confirmationText.trim().ifEmpty { "Batería desconectada" }
            } else {
                ""
            }
            val alertHeading = when (s.selectedAlertLevel) {
                "Informativa" -> "Nota"
                "Peligro eléctrico" -> "Precaución"
                else -> "Advertencia"
            }
            GuideStep(
                id = "${newGuideId}_step_${idx + 1}",
                guideId = newGuideId,
                stepNumber = idx + 1,
                title = s.title.trim(),
                description = s.description.trim().ifEmpty { "Realiza el procedimiento indicado con precaución." },
                hasAlert = s.includeCautionAlert,
                alertType = s.selectedAlertLevel,
                alertTitle = alertHeading,
                alertMessage = s.alertText.trim().ifEmpty { "Verifica las medidas de seguridad antes de continuar." },
                requiresConfirmation = confirmEnabled,
                confirmationText = confirmLabel,
                imageUrl = s.imageUri
            )
        }

        val matchedDevice = availableDevices.find { it.id == current.formSelectedDeviceId }
            ?: availableDevices.find {
                current.formModelSpec.isNotBlank() &&
                    "${it.name} ${it.model}".equals(current.formModelSpec.trim(), ignoreCase = true)
            }
            ?: availableDevices.firstOrNull {
                it.category.equals(category, ignoreCase = true) && it.authorId == Constants.DEFAULT_TECH_ID
            }

        val targetDeviceId = matchedDevice?.id ?: if (current.formSelectedDeviceId.isNotBlank()) {
            current.formSelectedDeviceId
        } else if (category == "PC") {
            "dev_2"
        } else {
            "dev_1"
        }

        val resolvedModelSpec = current.formModelSpec.trim().ifEmpty {
            matchedDevice?.let { "${it.name} ${it.model}" } ?: "Lenovo LOQ 15IRH8"
        }

        val pendingTool = current.formToolInput.trim()
        val combinedTools = if (pendingTool.isNotEmpty() && !current.formToolsList.contains(pendingTool)) {
            current.formToolsList + pendingTool
        } else {
            current.formToolsList
        }

        val techName = authRepository.currentTech.value.fullName.ifBlank { Constants.DEFAULT_TECH_NAME }
        val newGuide = Guide(
            id = newGuideId,
            deviceId = targetDeviceId,
            title = title,
            category = category,
            difficulty = difficultyNormalized,
            estimatedTime = current.formEstimatedTime.trim().ifEmpty { "30 min" },
            technicianId = Constants.DEFAULT_TECH_ID,
            technicianName = techName,
            modelSpec = resolvedModelSpec,
            precautions = "Procedimiento técnico paso a paso para $title.",
            toolsList = combinedTools.ifEmpty { listOf("Destornillador Phillips #0", "Púa de apertura") },
            imageUrl = current.formCoverImageUri,
            steps = steps
        )

        viewModelScope.launch {
            try {
                guideRepository.insertGuideWithSteps(newGuide)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al publicar la guía") }
            }
        }

        _uiState.update {
            it.copy(
                formGuideTitle = "",
                formEstimatedTime = "",
                formModelSpec = "",
                formCoverImageUri = "",
                formCoverImageFileName = "",
                formToolInput = "",
                formToolsList = emptyList(),
                formStepCount = 1,
                formSteps = listOf(StepFormState()),
                errorMessage = null
            )
        }
        return true
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
