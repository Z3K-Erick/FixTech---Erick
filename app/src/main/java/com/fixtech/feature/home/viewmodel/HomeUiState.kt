package com.fixtech.feature.home.viewmodel

data class HomeUiState(
    val searchQuery: String = "",
    val deviceSearchQuery: String = "",
    val selectedTab: Int = 0, // 0: Mis Equipos, 1: Explorar, 2: Perfil
    val exploreSelectedCategory: String? = null, // null = Categorías (Laptop / PC), non-null = Lista de Guías
    val exploreSearchQuery: String = "",

    // Add Device / Configuración de equipo
    val newDeviceType: String = "Laptop",
    val newDeviceBrand: String = "",
    val newDeviceModel: String = "",
    val newDeviceRam: String = "8 GB",
    val newDeviceStorage: String = "512 GB SSD",
    val newDeviceProcessor: String = "Intel Core",

    // Tech Home State
    val techSearchQuery: String = "",
    val techSelectedTab: Int = 0, // 0: Mis Equipos, 1: Guías, 2: Perfil
    val techSelectedFilter: String = "Todos",
    val isLoading: Boolean = false
)
