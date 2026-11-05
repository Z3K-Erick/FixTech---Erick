package com.fixtech.feature.home.viewmodel

data class HomeUiState(
    val searchQuery: String = "",
    val selectedTab: Int = 1, // 0: Mis Equipos, 1: Explorar, 2: Perfil
    val techSearchQuery: String = "",
    val techSelectedTab: Int = 0, // 0: Mis Equipos, 1: Guías, 2: Perfil
    val techSelectedFilter: String = "Todos",
    val isLoading: Boolean = false
)
