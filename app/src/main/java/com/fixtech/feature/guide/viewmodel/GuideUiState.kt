package com.fixtech.feature.guide.viewmodel

data class StepFormState(
    val title: String = "",
    val description: String = "",
    val includeCautionAlert: Boolean = true,
    val selectedAlertLevel: String = "Precaución",
    val alertText: String = "",
    val requiresConfirmation: Boolean = false,
    val confirmationText: String = ""
)

data class GuideStepItem(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val hasAlert: Boolean = true,
    val alertType: String = "Precaución", // "Informativa", "Precaución", "Peligro eléctrico"
    val alertTitle: String = "Precaución",
    val alertMessage: String = "",
    val requiresConfirmation: Boolean = false,
    val confirmationText: String = ""
) {
    // Aliases for compatibility
    val alertLevel: String get() = alertType
    val alertDescription: String get() = alertMessage
}

data class GuideItem(
    val id: String,
    val deviceId: String,
    val title: String,
    val category: String, // "Laptop" o "PC"
    val difficulty: String, // "Fácil", "Media", "Difícil"
    val estimatedTime: String,
    val authorId: String,
    val author: String,
    val modelSpec: String,
    val description: String,
    val tools: List<String>,
    val steps: List<GuideStepItem>,
    val isFavorite: Boolean = false
)

val defaultCatalogGuides = listOf(
    GuideItem(
        id = "guide_ram",
        deviceId = "dev_1",
        title = "Mejorar RAM o SSD",
        category = "Laptop",
        difficulty = "Media",
        estimatedTime = "25 min",
        authorId = "admin-current",
        author = "Carlos M.",
        modelSpec = "Lenovo LOQ 15IRH8",
        description = "Actualiza la memoria RAM o unidad SSD NVMe de tu portátil de forma segura protegiendo los componentes contra descargas electrostáticas.",
        tools = listOf("Destornillador Phillips #0", "Pulsera antiestática", "Púa de apertura"),
        steps = listOf(
            GuideStepItem(
                stepNumber = 1,
                title = "Apaga el equipo y desconecta la batería",
                instruction = "Asegúrate de apagar el equipo completamente. Luego abre la tapa trasera y jala suavemente el cable de la batería para desconectarlo.",
                hasAlert = true,
                alertType = "Peligro eléctrico",
                alertTitle = "Precaución",
                alertMessage = "No toques ninguna pieza sin antes desconectar la batería. Podrías dañar el equipo o lastimarte.",
                requiresConfirmation = true,
                confirmationText = "Batería desconectada"
            ),
            GuideStepItem(
                stepNumber = 2,
                title = "Ubica las ranuras de memoria",
                instruction = "Busca los módulos de RAM dentro del equipo. Son piezas rectangulares con seguros a los lados. Empuja las pestañas hacia afuera para soltarlos.",
                hasAlert = false,
                alertType = "Precaución",
                alertTitle = "Advertencia",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStepItem(
                stepNumber = 3,
                title = "Instala la nueva memoria o SSD",
                instruction = "Inserta el módulo nuevo inclinado 45° y luego presiónalo hasta escuchar un clic. Asegúrate de que quede bien fijo antes de cerrar el equipo.",
                hasAlert = true,
                alertType = "Precaución",
                alertTitle = "Advertencia",
                alertMessage = "Manipula la memoria tomándola por los bordes. No toques los contactos dorados con los dedos.",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    ),
    GuideItem(
        id = "guide_battery",
        deviceId = "dev_3",
        title = "Cambiar Batería",
        category = "Laptop",
        difficulty = "Fácil",
        estimatedTime = "20 min",
        authorId = "admin-other",
        author = "Ana R.",
        modelSpec = "Lenovo LOQ 15IRH8",
        description = "Reemplazo paso a paso de batería interna desgastada para restaurar la autonomía de tu laptop.",
        tools = listOf("Destornillador Phillips #0", "Púa de apertura"),
        steps = listOf(
            GuideStepItem(
                stepNumber = 1,
                title = "Retira los tornillos de la tapa",
                instruction = "Voltea el equipo y quita los tornillos de la tapa inferior con el destornillador. Guárdalos en un lugar seguro para no perderlos.",
                hasAlert = true,
                alertType = "Peligro eléctrico",
                alertTitle = "Precaución",
                alertMessage = "Verifica que el equipo esté totalmente apagado y sin el cargador conectado antes de abrir la tapa.",
                requiresConfirmation = true,
                confirmationText = "Equipo apagado y desconectado"
            ),
            GuideStepItem(
                stepNumber = 2,
                title = "Abre la tapa con la púa",
                instruction = "Desliza la púa por el borde del equipo para separar la tapa. Ve despacio para no quebrar los ganchos de plástico.",
                hasAlert = true,
                alertType = "Informativa",
                alertTitle = "Nota",
                alertMessage = "Si sientes mucha resistencia en algún punto, revisa que no falte quitar algún tornillo antes de forzar.",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStepItem(
                stepNumber = 3,
                title = "Cambia la batería",
                instruction = "Desconecta el cable de la batería vieja y sácala. Coloca la batería nueva, conecta el cable y vuelve a cerrar la tapa.",
                hasAlert = false,
                alertType = "Precaución",
                alertTitle = "Advertencia",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    ),
    GuideItem(
        id = "guide_cleaning",
        deviceId = "dev_2",
        title = "Limpieza Profunda",
        category = "PC",
        difficulty = "Difícil",
        estimatedTime = "45 min",
        authorId = "admin-current",
        author = "Carlos M.",
        modelSpec = "Corsair One i300",
        description = "Eliminación de polvo acumulado en ventiladores y cambio de pasta térmica en el procesador.",
        tools = listOf("Destornillador Phillips #0", "Pasta térmica", "Alcohol isopropílico", "Compresor de aire"),
        steps = listOf(
            GuideStepItem(
                stepNumber = 1,
                title = "Abre el equipo y desconecta la batería",
                instruction = "Quita los tornillos, abre la tapa y desconecta la energía antes de tocar cualquier pieza interna.",
                hasAlert = true,
                alertType = "Peligro eléctrico",
                alertTitle = "Precaución",
                alertMessage = "No uses el compresor cerca de los ojos y mantén el equipo sobre una superficie firme.",
                requiresConfirmation = true,
                confirmationText = "Batería desconectada"
            ),
            GuideStepItem(
                stepNumber = 2,
                title = "Sopla el polvo del ventilador",
                instruction = "Con el compresor de aire, sopla el ventilador y los conductos de ventilación en ráfagas cortas. Aleja el polvo del equipo.",
                hasAlert = false,
                alertType = "Precaución",
                alertTitle = "Advertencia",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStepItem(
                stepNumber = 3,
                title = "Cambia la pasta térmica",
                instruction = "Con un hisopo con alcohol, limpia la pasta vieja del procesador. Aplica una pequeña gota de pasta nueva en el centro y vuelve a colocar el disipador.",
                hasAlert = true,
                alertType = "Precaución",
                alertTitle = "Advertencia",
                alertMessage = "No apliques demasiada pasta. Una gota del tamaño de un grano de arroz es suficiente.",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    ),
    GuideItem(
        id = "guide_gpu",
        deviceId = "dev_4",
        title = "Revisión de GPU y Ventiladores",
        category = "PC",
        difficulty = "Media",
        estimatedTime = "30 min",
        authorId = "admin-other",
        author = "Ana R.",
        modelSpec = "Corsair One i300",
        description = "Inspección de tarjeta gráfica, limpieza de ventiladores con aire comprimido y verificación del slot PCIe.",
        tools = listOf("Destornillador Phillips #0", "Compresor de aire", "Pulsera antiestática"),
        steps = listOf(
            GuideStepItem(
                stepNumber = 1,
                title = "Retira la tapa lateral del gabinete",
                instruction = "Desatornilla los dos tornillos de la tapa lateral y deslízala hacia atrás para abrirla. Coloca el gabinete sobre una superficie estable.",
                hasAlert = true,
                alertType = "Precaución",
                alertTitle = "Advertencia",
                alertMessage = "Antes de abrir el gabinete, asegúrate de haber desconectado el cable de corriente completamente.",
                requiresConfirmation = true,
                confirmationText = "Cable desconectado"
            ),
            GuideStepItem(
                stepNumber = 2,
                title = "Limpia los ventiladores con aire comprimido",
                instruction = "Sostén las aspas del ventilador con un dedo para que no giren. Sopla en ráfagas cortas para expulsar el polvo acumulado sin dañar los rodamientos.",
                hasAlert = false,
                alertType = "Informativa",
                alertTitle = "Nota",
                alertMessage = "",
                requiresConfirmation = false,
                confirmationText = ""
            ),
            GuideStepItem(
                stepNumber = 3,
                title = "Verifica que la GPU esté bien encajada",
                instruction = "Empuja suavemente la tarjeta gráfica hacia abajo hasta escuchar el clic del seguro del slot PCIe. Reconecta los cables de alimentación de la GPU.",
                hasAlert = true,
                alertType = "Informativa",
                alertTitle = "Nota",
                alertMessage = "Si la GPU no hace clic al encajar, no la fuerces. Revisa que el slot esté libre de obstrucciones.",
                requiresConfirmation = false,
                confirmationText = ""
            )
        )
    )
)

data class GuideUiState(
    val guides: List<GuideItem> = defaultCatalogGuides,
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
    val isCurrentGuideFavorite: Boolean = defaultCatalogGuides.first().isFavorite,

    // Repair Step viewer
    val currentStep: Int = 1,
    val totalSteps: Int = defaultCatalogGuides.first().steps.size,
    val stepTitle: String = defaultCatalogGuides.first().steps.first().title,
    val stepInstruction: String = defaultCatalogGuides.first().steps.first().instruction,
    val stepHasAlert: Boolean = defaultCatalogGuides.first().steps.first().hasAlert,
    val stepAlertLevel: String = defaultCatalogGuides.first().steps.first().alertType,
    val cautionTitle: String = defaultCatalogGuides.first().steps.first().alertTitle,
    val cautionDescription: String = defaultCatalogGuides.first().steps.first().alertMessage,
    val stepRequiresConfirmation: Boolean = defaultCatalogGuides.first().steps.first().requiresConfirmation,
    val stepConfirmationText: String = defaultCatalogGuides.first().steps.first().confirmationText,
    val isStepAlertConfirmed: Boolean = false,
    val progress: Float = 1f / defaultCatalogGuides.first().steps.size.toFloat(),

    // Create Guide Form State
    val formGuideTitle: String = "",
    val formSelectedDeviceId: String = "dev_1",
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
