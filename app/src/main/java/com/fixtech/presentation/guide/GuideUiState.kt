package com.fixtech.presentation.guide

import com.fixtech.domain.model.Guide
import com.fixtech.domain.model.GuideStep

typealias GuideItem = Guide
typealias GuideStepItem = GuideStep

data class StepFormState(
    val title: String = "",
    val description: String = "",
    val imageUri: String = "",
    val imageFileName: String = "",
    val resourceUrl: String = "",
    val includeCautionAlert: Boolean = false,
    val selectedAlertLevel: String = "Advertencia",
    val alertText: String = "",
    val requiresConfirmation: Boolean = false,
    val confirmationText: String = "",
    val isTitleError: Boolean = false
)

val defaultCatalogGuides = listOf(
    Guide(
        id = "guide_ram",
        deviceId = "dev_1",
        title = "Mejorar RAM o SSD",
        category = "Laptop",
        difficulty = "Medio",
        estimatedTime = "25 min",
        technicianId = "admin-current",
        technicianName = "Carlos M.",
        modelSpec = "Lenovo LOQ 15IRH8",
        precautions = "Actualiza la memoria RAM o unidad SSD NVMe de tu portátil de forma segura protegiendo los componentes contra descargas electrostáticas.",
        toolsList = listOf("Destornillador Phillips #0", "Pulsera antiestática", "Púa de apertura"),
        steps = listOf(
            GuideStep(
                stepNumber = 1,
                title = "Apaga el equipo y desconecta la batería",
                description = "Asegúrate de apagar el equipo completamente. Luego abre la tapa trasera y jala suavemente el cable de la batería para desconectarlo.",
                hasAlert = true,
                alertType = "Peligro",
                alertTitle = "Peligro",
                alertMessage = "No toques ninguna pieza sin antes desconectar la batería. Podrías dañar el equipo o lastimarte.",
                requiresConfirmation = true,
                confirmationText = "Batería desconectada"
            ),
            GuideStep(
                stepNumber = 2,
                title = "Ubica las ranuras de memoria",
                description = "Busca los módulos de RAM dentro del equipo. Son piezas rectangulares con seguros a los lados. Empuja las pestañas hacia afuera para soltarlos.",
                hasAlert = false,
                alertType = "Advertencia",
                alertTitle = "Advertencia",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStep(
                stepNumber = 3,
                title = "Instala la nueva memoria o SSD",
                description = "Inserta el módulo nuevo inclinado 45° y luego presiónalo hasta escuchar un clic. Asegúrate de que quede bien fijo antes de cerrar el equipo.",
                hasAlert = true,
                alertType = "Advertencia",
                alertTitle = "Advertencia",
                alertMessage = "Manipula la memoria tomándola por los bordes. No toques los contactos dorados con los dedos.",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    ),
    Guide(
        id = "guide_battery",
        deviceId = "dev_3",
        title = "Cambiar Batería",
        category = "Laptop",
        difficulty = "Bajo",
        estimatedTime = "20 min",
        technicianId = "admin-other",
        technicianName = "Ana R.",
        modelSpec = "Lenovo LOQ 15IRH8",
        precautions = "Reemplazo paso a paso de batería interna desgastada para restaurar la autonomía de tu laptop.",
        toolsList = listOf("Destornillador Phillips #0", "Púa de apertura"),
        steps = listOf(
            GuideStep(
                stepNumber = 1,
                title = "Retira los tornillos de la tapa",
                description = "Voltea el equipo y quita los tornillos de la tapa inferior con el destornillador. Guárdalos en un lugar seguro para no perderlos.",
                hasAlert = true,
                alertType = "Peligro",
                alertTitle = "Peligro",
                alertMessage = "Verifica que el equipo esté totalmente apagado y sin el cargador conectado antes de abrir la tapa.",
                requiresConfirmation = true,
                confirmationText = "Equipo apagado y desconectado"
            ),
            GuideStep(
                stepNumber = 2,
                title = "Abre la tapa con la púa",
                description = "Desliza la púa por el borde del equipo para separar la tapa. Ve despacio para no quebrar los ganchos de plástico.",
                hasAlert = true,
                alertType = "Información",
                alertTitle = "Información",
                alertMessage = "Si sientes mucha resistencia en algún punto, revisa que no falte quitar algún tornillo antes de forzar.",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStep(
                stepNumber = 3,
                title = "Cambia la batería",
                description = "Desconecta el cable de la batería vieja y sácala. Coloca la batería nueva, conecta el cable y vuelve a cerrar la tapa.",
                hasAlert = false,
                alertType = "Advertencia",
                alertTitle = "Advertencia",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    ),
    Guide(
        id = "guide_cleaning",
        deviceId = "dev_2",
        title = "Limpieza Profunda",
        category = "PC",
        difficulty = "Alto",
        estimatedTime = "45 min",
        technicianId = "admin-current",
        technicianName = "Carlos M.",
        modelSpec = "Corsair One i300",
        precautions = "Eliminación de polvo acumulado en ventiladores y cambio de pasta térmica en el procesador.",
        toolsList = listOf("Destornillador Phillips #0", "Pasta térmica", "Alcohol isopropílico", "Compresor de aire"),
        steps = listOf(
            GuideStep(
                stepNumber = 1,
                title = "Abre el equipo y desconecta la batería",
                description = "Quita los tornillos, abre la tapa y desconecta la energía antes de tocar cualquier pieza interna.",
                hasAlert = true,
                alertType = "Peligro",
                alertTitle = "Peligro",
                alertMessage = "No uses el compresor cerca de los ojos y mantén el equipo sobre una superficie firme.",
                requiresConfirmation = true,
                confirmationText = "Batería desconectada"
            ),
            GuideStep(
                stepNumber = 2,
                title = "Sopla el polvo del ventilador",
                description = "Con el compresor de aire, sopla el ventilador y los conductos de ventilación en ráfagas cortas. Aleja el polvo del equipo.",
                hasAlert = false,
                alertType = "Advertencia",
                alertTitle = "Advertencia",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStep(
                stepNumber = 3,
                title = "Cambia la pasta térmica",
                description = "Con un hisopo con alcohol, limpia la pasta vieja del procesador. Aplica una pequeña gota de pasta nueva en el centro y vuelve a colocar el disipador.",
                hasAlert = true,
                alertType = "Advertencia",
                alertTitle = "Advertencia",
                alertMessage = "No apliques demasiada pasta. Una gota del tamaño de un grano de arroz es suficiente.",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    ),
    Guide(
        id = "guide_gpu",
        deviceId = "dev_4",
        title = "Revisión de GPU y Ventiladores",
        category = "PC",
        difficulty = "Medio",
        estimatedTime = "30 min",
        technicianId = "admin-other",
        technicianName = "Ana R.",
        modelSpec = "Corsair One i300",
        precautions = "Inspección de tarjeta gráfica, limpieza de ventiladores con aire comprimido y verificación del slot PCIe.",
        toolsList = listOf("Destornillador Phillips #0", "Compresor de aire", "Pulsera antiestática"),
        steps = listOf(
            GuideStep(
                stepNumber = 1,
                title = "Retira la tapa lateral del gabinete",
                description = "Desatornilla los dos tornillos de la tapa lateral y deslízala hacia atrás para abrirla. Coloca el gabinete sobre una superficie estable.",
                hasAlert = true,
                alertType = "Advertencia",
                alertTitle = "Advertencia",
                alertMessage = "Antes de abrir el gabinete, asegúrate de haber desconectado el cable de corriente completamente.",
                requiresConfirmation = true,
                confirmationText = "Cable desconectado"
            ),
            GuideStep(
                stepNumber = 2,
                title = "Limpia los ventiladores con aire comprimido",
                description = "Sostén las aspas del ventilador con un dedo para que no giren. Sopla en ráfagas cortas para expulsar el polvo acumulado sin dañar los rodamientos.",
                hasAlert = false,
                alertType = "Información",
                alertTitle = "Información",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStep(
                stepNumber = 3,
                title = "Verifica que la GPU esté bien encajada",
                description = "Empuja suavemente la tarjeta gráfica hacia abajo hasta escuchar el clic del seguro del slot PCIe. Reconecta los cables de alimentación de la GPU.",
                hasAlert = true,
                alertType = "Información",
                alertTitle = "Información",
                alertMessage = "Si la GPU no hace clic al encajar, no la fuerces. Revisa que el slot esté libre de obstrucciones.",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    )
)

data class GuideUiState(
    val guides: List<Guide> = defaultCatalogGuides,
    // Lista de guías para el dispositivo activo, ordenadas por Room (Playlist Priority)
    val deviceGuides: List<Guide> = emptyList(),
    val activeDeviceId: String = "dev_1",
    val selectedGuideId: String = defaultCatalogGuides.first().id,

    // Guide Detail labels (bound to selected guide)
    val title: String = defaultCatalogGuides.first().title,
    val categoryBadge: String = defaultCatalogGuides.first().category,
    val estimatedTimeLabel: String = defaultCatalogGuides.first().estimatedTime,
    val difficultyLabel: String = defaultCatalogGuides.first().difficulty,
    val authorId: String = defaultCatalogGuides.first().authorId,
    val authorLabel: String = defaultCatalogGuides.first().author,
    val description: String = defaultCatalogGuides.first().description,
    val tools: List<String> = defaultCatalogGuides.first().tools,
    val imageUrl: String = defaultCatalogGuides.first().imageUrl,
    val isCurrentGuideFavorite: Boolean = defaultCatalogGuides.first().isFavorite,

    // Repair Step viewer
    val currentStep: Int = 1,
    val totalSteps: Int = defaultCatalogGuides.first().steps.size,
    val stepTitle: String = defaultCatalogGuides.first().steps.first().title,
    val stepInstruction: String = defaultCatalogGuides.first().steps.first().instruction,
    val stepImageUrl: String = defaultCatalogGuides.first().steps.first().imageUrl,
    val stepImageUri: String? = defaultCatalogGuides.first().steps.first().imageUri,
    val stepResourceUrl: String? = defaultCatalogGuides.first().steps.first().resourceUrl,
    val stepHasAlert: Boolean = defaultCatalogGuides.first().steps.first().hasAlert,
    val stepAlertLevel: String = defaultCatalogGuides.first().steps.first().alertType,
    val cautionTitle: String = defaultCatalogGuides.first().steps.first().alertTitle,
    val cautionDescription: String = defaultCatalogGuides.first().steps.first().alertMessage,
    val stepRequiresConfirmation: Boolean = defaultCatalogGuides.first().steps.first().requiresConfirmation,
    val stepConfirmationText: String = defaultCatalogGuides.first().steps.first().confirmationText,
    val isStepAlertConfirmed: Boolean = false,
    val progress: Float = 1f / defaultCatalogGuides.first().steps.size.toFloat(),

    // Create / Edit Guide Form State
    val editingGuideId: String? = null,
    val formGuideTitle: String = "",
    val formSelectedDeviceId: String = "dev_1",
    val formSelectedCategory: String = "Laptop",
    val formSelectedDifficulty: String = "Medio",
    val formEstimatedTime: String = "",
    val formModelSpec: String = "",
    val formCoverImageUri: String = "",
    val formCoverImageFileName: String = "",
    val formToolInput: String = "",
    val formToolsList: List<String> = emptyList(),
    val formStepCount: Int = 1,
    val formSteps: List<StepFormState> = listOf(StepFormState()),
    val showMandatoryValidationBanner: Boolean = false,
    val isGuideTitleError: Boolean = false,
    val isGuideDeviceError: Boolean = false,
    val isStepsError: Boolean = false,
    val isSavingGuide: Boolean = false,

    // UI flags obligatorios
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
