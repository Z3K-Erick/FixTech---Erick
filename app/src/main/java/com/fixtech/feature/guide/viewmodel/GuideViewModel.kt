package com.fixtech.feature.guide.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GuideViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    fun nextStep() {
        _uiState.update { current ->
            if (current.currentStep < current.totalSteps) {
                val next = current.currentStep + 1
                current.copy(
                    currentStep = next,
                    progress = next.toFloat() / current.totalSteps.toFloat()
                )
            } else {
                current
            }
        }
    }

    fun previousStep() {
        _uiState.update { current ->
            if (current.currentStep > 1) {
                val prev = current.currentStep - 1
                current.copy(
                    currentStep = prev,
                    progress = prev.toFloat() / current.totalSteps.toFloat()
                )
            } else {
                current
            }
        }
    }

    fun resetSteps() {
        _uiState.update { current ->
            current.copy(
                currentStep = 1,
                progress = 1f / current.totalSteps.toFloat()
            )
        }
    }

    // Create Guide Form handlers
    fun onFormGuideTitleChanged(title: String) {
        _uiState.update { it.copy(formGuideTitle = title) }
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
                val nextIndex = current.formToolsList.size + 1
                current.copy(
                    formToolsList = current.formToolsList + "Herramienta #$nextIndex"
                )
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
                current.copy(formSteps = updated)
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
}
