package com.fixtech.feature.guide.viewmodel

data class GuideUiState(
    val title: String = "Título de la Guía",
    val description: String = "",
    val currentStep: Int = 1,
    val totalSteps: Int = 5,
    val stepInstruction: String = "Instrucción del paso",
    val progress: Float = 0.2f,
    val isLoading: Boolean = false
)
