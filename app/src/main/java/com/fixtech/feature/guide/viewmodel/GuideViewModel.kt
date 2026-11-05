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
}
