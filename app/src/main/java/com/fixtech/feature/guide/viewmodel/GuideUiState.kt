package com.fixtech.feature.guide.viewmodel

data class StepFormState(
    val title: String = "",
    val description: String = "",
    val includeCautionAlert: Boolean = true,
    val selectedAlertLevel: String = "Precaución",
    val alertText: String = ""
)

data class GuideUiState(
    // Guide Detail structural labels
    val title: String = "Mantenimiento Preventivo",
    val categoryBadge: String = "Laptop",
    val estimatedTimeLabel: String = "Tiempo estimado",
    val difficultyLabel: String = "Dificultad",
    val authorLabel: String = "Autor técnico",
    val description: String = "Descripción estructural del procedimiento técnico paso a paso.",

    // Repair Step viewer (1 to 4)
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val stepTitle: String = "Título estructural del paso",
    val stepInstruction: String = "Descripción estructural detallada de la acción a realizar en este paso del procedimiento técnico.",
    val cautionTitle: String = "Precaución / Importante",
    val cautionDescription: String = "Indicación estructural de seguridad y prevención de daños al componente antes de continuar.",
    val progress: Float = 0.25f,

    // Create Guide Form State
    val formGuideTitle: String = "",
    val formSelectedCategory: String = "Laptop",
    val formSelectedDifficulty: String = "Intermedio",
    val formEstimatedTime: String = "",
    val formModelSpec: String = "",
    val formToolInput: String = "",
    val formToolsList: List<String> = emptyList(),
    val formStepCount: Int = 1,
    val formSteps: List<StepFormState> = listOf(StepFormState()),

    val isLoading: Boolean = false
)
