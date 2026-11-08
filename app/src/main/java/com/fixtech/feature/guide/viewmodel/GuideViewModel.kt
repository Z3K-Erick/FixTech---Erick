package com.fixtech.feature.guide.viewmodel

import androidx.lifecycle.ViewModel
import com.fixtech.feature.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val sharedGuideState = MutableStateFlow(GuideUiState())
private val favoriteGuideIdsByUser = mutableMapOf<String, Set<String>>()
private var activeGuideUserKey: String = "default_user"

class GuideViewModel : ViewModel() {

    val uiState: StateFlow<GuideUiState> = sharedGuideState.asStateFlow()

    fun selectGuide(guideId: String) {
        sharedGuideState.update { current ->
            val guide = current.guides.find { it.id == guideId } ?: current.guides.first()
            val firstStep = guide.steps.firstOrNull() ?: GuideStepItem(
                stepNumber = 1,
                title = "Paso 1",
                instruction = "Sigue las instrucciones indicadas.",
                alertType = "Precaución",
                alertTitle = "Precaución",
                alertMessage = "Procede con cuidado."
            )
            val total = guide.steps.size.coerceAtLeast(1)
            current.copy(
                selectedGuideId = guide.id,
                title = guide.title,
                categoryBadge = guide.category,
                estimatedTimeLabel = guide.estimatedTime,
                difficultyLabel = guide.difficulty,
                authorId = guide.authorId,
                authorLabel = guide.author,
                description = guide.description,
                tools = guide.tools,
                isCurrentGuideFavorite = guide.isFavorite,
                currentStep = 1,
                totalSteps = total,
                stepTitle = firstStep.title,
                stepInstruction = firstStep.instruction,
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

    fun toggleFavoriteGuide(guideId: String = sharedGuideState.value.selectedGuideId) {
        sharedGuideState.update { current ->
            val updatedGuides = current.guides.map { guide ->
                if (guide.id == guideId) guide.copy(isFavorite = !guide.isFavorite) else guide
            }
            val currentFavIds = updatedGuides.filter { it.isFavorite }.map { it.id }.toSet()
            favoriteGuideIdsByUser[activeGuideUserKey] = currentFavIds
            // Count only valid existing guides
            val favCount = updatedGuides.count { it.isFavorite }
            ProfileViewModel.updateFavoriteGuidesCount(favCount)
            val selectedGuide = updatedGuides.find { it.id == current.selectedGuideId }
            current.copy(
                guides = updatedGuides,
                isCurrentGuideFavorite = selectedGuide?.isFavorite ?: current.isCurrentGuideFavorite
            )
        }
    }

    fun toggleStepAlertConfirmation() {
        sharedGuideState.update { current ->
            current.copy(isStepAlertConfirmed = !current.isStepAlertConfirmed)
        }
    }

    fun setStepAlertConfirmation(confirmed: Boolean) {
        sharedGuideState.update { current ->
            current.copy(isStepAlertConfirmed = confirmed)
        }
    }

    fun nextStep() {
        sharedGuideState.update { current ->
            val guide = current.guides.find { it.id == current.selectedGuideId } ?: current.guides.first()
            if (current.currentStep < current.totalSteps) {
                val next = current.currentStep + 1
                val stepObj = guide.steps.getOrNull(next - 1)
                current.copy(
                    currentStep = next,
                    stepTitle = stepObj?.title ?: current.stepTitle,
                    stepInstruction = stepObj?.instruction ?: current.stepInstruction,
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
        sharedGuideState.update { current ->
            val guide = current.guides.find { it.id == current.selectedGuideId } ?: current.guides.first()
            if (current.currentStep > 1) {
                val prev = current.currentStep - 1
                val stepObj = guide.steps.getOrNull(prev - 1)
                current.copy(
                    currentStep = prev,
                    stepTitle = stepObj?.title ?: current.stepTitle,
                    stepInstruction = stepObj?.instruction ?: current.stepInstruction,
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
        selectGuide(sharedGuideState.value.selectedGuideId)
    }

    // Create Guide Form handlers
    fun onFormGuideTitleChanged(title: String) {
        sharedGuideState.update { it.copy(formGuideTitle = title) }
    }

    fun onFormDeviceSelected(deviceId: String, category: String, modelSpec: String) {
        sharedGuideState.update {
            it.copy(
                formSelectedDeviceId = deviceId,
                formSelectedCategory = category,
                formModelSpec = modelSpec
            )
        }
    }

    fun onFormCategorySelected(category: String) {
        sharedGuideState.update { it.copy(formSelectedCategory = category) }
    }

    fun onFormDifficultySelected(difficulty: String) {
        sharedGuideState.update { it.copy(formSelectedDifficulty = difficulty) }
    }

    fun onFormEstimatedTimeChanged(time: String) {
        sharedGuideState.update { it.copy(formEstimatedTime = time) }
    }

    fun onFormModelSpecChanged(model: String) {
        sharedGuideState.update { it.copy(formModelSpec = model) }
    }

    fun onFormToolInputChanged(input: String) {
        sharedGuideState.update { it.copy(formToolInput = input) }
    }

    fun addFormTool() {
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
            val updatedList = current.formSteps + StepFormState()
            current.copy(
                formStepCount = updatedList.size,
                formSteps = updatedList
            )
        }
    }

    fun removeStepBlock(index: Int) {
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(title = title)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepDescriptionChanged(index: Int, description: String) {
        sharedGuideState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(description = description)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun onStepCautionToggled(index: Int, enabled: Boolean) {
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
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
        sharedGuideState.update { current ->
            if (index in current.formSteps.indices) {
                val updated = current.formSteps.toMutableList()
                updated[index] = updated[index].copy(confirmationText = text)
                current.copy(formSteps = updated)
            } else {
                current
            }
        }
    }

    fun publishGuide() {
        sharedGuideState.update { current ->
            val title = current.formGuideTitle.trim().ifEmpty { "Guía de Mantenimiento" }
            val category = if (current.formSelectedCategory.contains("PC", ignoreCase = true)) "PC" else "Laptop"
            val difficultyNormalized = when (current.formSelectedDifficulty) {
                "Básico", "Bajo", "Fácil" -> "Fácil"
                "Avanzado", "Alto", "Difícil" -> "Difícil"
                else -> "Media"
            }
            val steps = current.formSteps.mapIndexed { idx, s ->
                val confirmEnabled = s.includeCautionAlert && s.requiresConfirmation
                val confirmLabel = if (confirmEnabled) {
                    s.confirmationText.trim().ifEmpty { "Batería desconectada" }
                } else {
                    ""
                }
                GuideStepItem(
                    stepNumber = idx + 1,
                    title = s.title.trim().ifEmpty { "Paso ${idx + 1}" },
                    instruction = s.description.trim().ifEmpty { "Realiza el procedimiento indicado con precaución." },
                    hasAlert = s.includeCautionAlert,
                    alertType = s.selectedAlertLevel,
                    alertTitle = s.selectedAlertLevel,
                    alertMessage = s.alertText.trim().ifEmpty { "Verifica las medidas de seguridad antes de continuar." },
                    requiresConfirmation = confirmEnabled,
                    confirmationText = confirmLabel
                )
            }
            val targetDeviceId = if (current.formSelectedDeviceId.isNotBlank()) {
                current.formSelectedDeviceId
            } else if (category == "PC") {
                "dev_2"
            } else {
                "dev_1"
            }
            val newGuide = GuideItem(
                id = "guide_${System.currentTimeMillis()}",
                deviceId = targetDeviceId,
                title = title,
                category = category,
                difficulty = difficultyNormalized,
                estimatedTime = current.formEstimatedTime.trim().ifEmpty { "30 min" },
                authorId = "admin-current",
                author = ProfileViewModel.getActiveTechName(),
                modelSpec = current.formModelSpec.trim().ifEmpty { "Lenovo LOQ 15IRH8" },
                description = "Procedimiento técnico paso a paso para $title.",
                tools = current.formToolsList.ifEmpty { listOf("Destornillador Phillips #0", "Púa de apertura") },
                steps = steps
            )
            ProfileViewModel.incrementTechGuidesCount()
            current.copy(
                guides = current.guides + newGuide,
                formGuideTitle = "",
                formEstimatedTime = "",
                formModelSpec = "",
                formToolInput = "",
                formToolsList = emptyList(),
                formStepCount = 1,
                formSteps = listOf(StepFormState())
            )
        }
    }

    companion object {
        fun switchUserSession(email: String) {
            val key = email.trim().lowercase().ifEmpty { "default_user" }
            activeGuideUserKey = key
            val favIds = favoriteGuideIdsByUser[key] ?: emptySet()
            sharedGuideState.update { current ->
                val updatedGuides = current.guides.map { guide ->
                    guide.copy(isFavorite = guide.id in favIds)
                }
                val validFavCount = updatedGuides.count { it.isFavorite }
                ProfileViewModel.updateFavoriteGuidesCount(validFavCount)
                val selected = updatedGuides.find { it.id == current.selectedGuideId }
                current.copy(
                    guides = updatedGuides,
                    isCurrentGuideFavorite = selected?.isFavorite ?: false
                )
            }
        }

        fun clearUserSession() {
            activeGuideUserKey = "logged_out"
            sharedGuideState.update { current ->
                val resetGuides = current.guides.map { it.copy(isFavorite = false) }
                ProfileViewModel.updateFavoriteGuidesCount(0)
                current.copy(
                    guides = resetGuides,
                    isCurrentGuideFavorite = false
                )
            }
        }
    }
}
