package com.fixtech.feature.home.viewmodel

data class UserDeviceItem(
    val id: String,
    val name: String,
    val model: String,
    val category: String, // "Laptop" o "PC"
    val authorId: String = "admin-current",
    val authorName: String = "Carlos M.",
    val ram: String = "16 GB",
    val storage: String = "512 GB SSD",
    val processor: String = "Intel Core"
)

data class PublicCertificateItem(
    val id: String,
    val title: String,
    val institution: String,
    val fileName: String
)

data class DiagnosticOption(
    val id: String,
    val title: String,
    val description: String,
    val recommendedGuideId: String
)

val defaultCatalogDevices = listOf(
    UserDeviceItem(
        id = "dev_1",
        name = "Lenovo LOQ",
        model = "15IRH8",
        category = "Laptop",
        authorId = "admin-current",
        authorName = "Carlos M.",
        ram = "16 GB",
        storage = "512 GB SSD",
        processor = "Intel Core i5"
    ),
    UserDeviceItem(
        id = "dev_2",
        name = "Corsair One",
        model = "i300",
        category = "PC",
        authorId = "admin-current",
        authorName = "Carlos M.",
        ram = "32 GB",
        storage = "1 TB SSD",
        processor = "Intel Core i9"
    ),
    UserDeviceItem(
        id = "dev_3",
        name = "Lenovo LOQ",
        model = "15IRH8",
        category = "Laptop",
        authorId = "admin-other",
        authorName = "Ana R.",
        ram = "16 GB",
        storage = "512 GB SSD",
        processor = "AMD Ryzen 7"
    ),
    UserDeviceItem(
        id = "dev_4",
        name = "Corsair One",
        model = "i300",
        category = "PC",
        authorId = "admin-other",
        authorName = "Ana R.",
        ram = "32 GB",
        storage = "1 TB SSD",
        processor = "Intel Core i7"
    )
)

val defaultTechCertificates: Map<String, List<PublicCertificateItem>> = mapOf(
    "admin-current" to listOf(
        PublicCertificateItem(
            id = "cert_1",
            title = "Mantenimiento de equipos de cómputo",
            institution = "Instituto TecnoSur",
            fileName = "mantenimiento.pdf"
        ),
        PublicCertificateItem(
            id = "cert_2",
            title = "Certificación en Electrónica y Microsoldadura",
            institution = "Centro de Capacitación Técnica",
            fileName = "electronica_cert.pdf"
        )
    ),
    "admin-other" to listOf(
        PublicCertificateItem(
            id = "cert_3",
            title = "Diagnóstico y Ensamble de Laptops y PC",
            institution = "Academia Nacional de Hardware",
            fileName = "diagnostico_hardware.pdf"
        )
    )
)

data class HomeUiState(
    val searchQuery: String = "",
    val deviceSearchQuery: String = "",
    val selectedTab: Int = 0, // 0: Mis Equipos, 1: Explorar, 2: Perfil
    val exploreSelectedCategory: String? = null,
    val exploreSearchQuery: String = "",

    // Catálogo global de equipos registrados por los técnicos
    val catalogDevices: List<UserDeviceItem> = defaultCatalogDevices,
    // IDs de equipos guardados (Bookmark) por el usuario actual
    val savedDeviceIds: Set<String> = emptySet(),
    // Equipo seleccionado para ver sus guías en DeviceGuidesScreen (TaskSelectionView)
    val selectedDeviceId: String = defaultCatalogDevices.first().id,
    // Técnico seleccionado para ver su perfil público en PublicTechProfileScreen
    val selectedPublicTechId: String = "admin-current",
    val techCertificatesByAdmin: Map<String, List<PublicCertificateItem>> = defaultTechCertificates,

    // Compatibilidad
    val userDevices: List<UserDeviceItem> = emptyList(),

    // Add Device (Exclusivo para Técnico)
    val newDeviceType: String = "Laptop",
    val newDeviceBrand: String = "",
    val newDeviceModel: String = "",
    val newDeviceRam: String = "8 GB",
    val newDeviceStorage: String = "512 GB SSD",
    val newDeviceProcessor: String = "Intel Core",
    val isBrandError: Boolean = false,
    val isModelError: Boolean = false,

    // Diagnostic Flow State
    val diagnosticStep: Int = 1,
    val selectedSymptomId: String = "symptom_thermal",
    val selectedDetailId: String = "detail_load",

    // Tech Home State
    val techSearchQuery: String = "",
    val techSelectedTab: Int = 0, // 0: Mis Equipos, 1: Guías, 2: Perfil
    val techSelectedFilter: String = "Todos",
    val isLoading: Boolean = false
)
