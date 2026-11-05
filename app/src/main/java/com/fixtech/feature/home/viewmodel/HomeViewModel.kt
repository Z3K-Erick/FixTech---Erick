package com.fixtech.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onTechSearchQueryChanged(query: String) {
        _uiState.update { it.copy(techSearchQuery = query) }
    }

    fun onTechTabSelected(index: Int) {
        _uiState.update { it.copy(techSelectedTab = index) }
    }

    fun onTechFilterSelected(filter: String) {
        _uiState.update { it.copy(techSelectedFilter = filter) }
    }
}
