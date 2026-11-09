package com.fixtech.presentation.home

import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.Device

typealias UserDeviceItem = Device
typealias PublicCertificateItem = Certificate

data class DiagnosticOption(
    val id: String,
    val title: String,
    val description: String,
    val recommendedGuideId: String
)

val defaultCatalogDevices = listOf(
    Device(
        id = "dev_1",
        name = "Lenovo LOQ",
        model = "15IRH8",
        type = "Laptop",
        technicianId = "admin-current",
        technicianName = "Carlos M.",
        ram = "16 GB",
        storage = "512 GB SSD",
        processor = "Intel Core i5",
        guidesCount = 1
    ),
    Device(
        id = "dev_2",
        name = "Corsair One",
        model = "i300",
        type = "PC",
        technicianId = "admin-current",
        technicianName = "Carlos M.",
        ram = "32 GB",
        storage = "1 TB SSD",
        processor = "Intel Core i9",
        guidesCount = 1
    ),
    Device(
        id = "dev_3",
        name = "Lenovo LOQ",
        model = "15IRH8",
        type = "Laptop",
        technicianId = "admin-other",
        technicianName = "Ana R.",
        ram = "16 GB",
        storage = "512 GB SSD",
        processor = "AMD Ryzen 7",
        guidesCount = 1
    ),
    Device(
        id = "dev_4",
        name = "Corsair One",
        model = "i300",
        type = "PC",
        technicianId = "admin-other",
        technicianName = "Ana R.",
        ram = "32 GB",
        storage = "1 TB SSD",
        processor = "Intel Core i7",
        guidesCount = 1
    )
)

val defaultTechCertificates: Map<String, List<Certificate>> = emptyMap()

data class HomeUiState(
    val searchQuery: String = "",
    val deviceSearchQuery: String = "",
    val selectedTab: Int = 0, // 0: Mis Equipos, 1: Explorar, 2: Perfil
    val exploreSelectedCategory: String? = null,
    val exploreSearchQuery: String = "",

    // Catálogo global de equipos registrados en Room ("Explorar")
    val catalogDevices: List<Device> = defaultCatalogDevices,
    // Dispositivos guardados por el usuario ordenados por savedAt DESC ("Mis Equipos")
    val mySavedDevices: List<Device> = emptyList(),
    // IDs de equipos guardados (Bookmark) por el usuario actual
    val savedDeviceIds: Set<String> = emptySet(),
    // Equipo seleccionado para ver sus guías en DeviceGuidesScreen
    val selectedDeviceId: String = defaultCatalogDevices.first().id,
    // Técnico seleccionado para ver su perfil público en PublicTechProfileScreen
    val selectedPublicTechId: String = "admin-current",
    val techCertificatesByAdmin: Map<String, List<Certificate>> = defaultTechCertificates,

    // Compatibilidad
    val userDevices: List<Device> = emptyList(),

    // Add / Edit Device (Exclusivo para Técnico)
    val editingDeviceId: String? = null,
    val newDeviceType: String = "Laptop",
    val newDeviceBrand: String = "",
    val newDeviceModel: String = "",
    val newDeviceImageUri: String = "",
    val newDeviceImageFileName: String = "",
    val newDeviceRam: String = "16 GB",
    val newDeviceStorage: String = "512 GB SSD",
    val newDeviceProcessor: String = "Intel Core",
    val isBrandError: Boolean = false,
    val isModelError: Boolean = false,
    val isDuplicateModelError: Boolean = false,
    val brandErrorText: String? = null,
    val modelErrorText: String? = null,

    // Diagnostic Flow State
    val diagnosticStep: Int = 1,
    val selectedSymptomId: String = "symptom_thermal",
    val selectedDetailId: String = "detail_load",

    // Tech Home State
    val techSearchQuery: String = "",
    val techSelectedTab: Int = 0, // 0: Mis Equipos, 1: Guías, 2: Perfil
    val techSelectedFilter: String = "Todos",
    val expandedTechDeviceId: String? = null,

    // UI flags obligatorios (Bloque 3)
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
