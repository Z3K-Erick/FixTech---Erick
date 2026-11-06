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

    fun onDeviceSearchQueryChanged(query: String) {
        _uiState.update { it.copy(deviceSearchQuery = query) }
    }

    fun onExploreSearchQueryChanged(query: String) {
        _uiState.update { it.copy(exploreSearchQuery = query) }
    }

    fun onExploreCategorySelected(category: String?) {
        _uiState.update { it.copy(exploreSelectedCategory = category) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    // Add Device handlers
    fun onNewDeviceTypeSelected(type: String) {
        _uiState.update { it.copy(newDeviceType = type) }
    }

    fun onNewDeviceBrandChanged(brand: String) {
        _uiState.update { it.copy(newDeviceBrand = brand) }
    }

    fun onNewDeviceModelChanged(model: String) {
        _uiState.update { it.copy(newDeviceModel = model) }
    }

    fun onNewDeviceRamSelected(ram: String) {
        _uiState.update { it.copy(newDeviceRam = ram) }
    }

    fun onNewDeviceStorageSelected(storage: String) {
        _uiState.update { it.copy(newDeviceStorage = storage) }
    }

    fun onNewDeviceProcessorSelected(processor: String) {
        _uiState.update { it.copy(newDeviceProcessor = processor) }
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
