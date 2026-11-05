package com.fixtech.feature.home.viewmodel

data class HomeUiState(
    val searchQuery: String = "",
    val selectedTab: Int = 1, // 0: Mis Equipos, 1: Explorar, 2: Perfil
    val isLoading: Boolean = false
)
