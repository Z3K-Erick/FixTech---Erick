package com.fixtech.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixtech.core.common.Constants
import com.fixtech.domain.model.Device
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.DeviceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val deviceRepository: DeviceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeRoomStreams()
    }

    private fun observeRoomStreams() {
        // 1. Observar catálogo de dispositivos ("Explorar") desde Room con isBookmarked y conteo de guías
        viewModelScope.launch {
            authRepository.observeCurrentUserId()
                .flatMapLatest { userId ->
                    deviceRepository.observeExploreDevices(userId)
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { exploreDevices ->
                    if (exploreDevices.isNotEmpty()) {
                        val bookmarkedIds = exploreDevices.filter { it.isBookmarked }.map { it.id }.toSet()
                        _uiState.update { state ->
                            state.copy(
                                catalogDevices = exploreDevices,
                                savedDeviceIds = bookmarkedIds,
                                isLoading = false
                            )
                        }
                    }
                }
        }

        // 2. Observar "Mis Equipos" guardados por el usuario ordenados por savedAt DESC desde Room
        viewModelScope.launch {
            authRepository.observeCurrentUserId()
                .flatMapLatest { userId ->
                    deviceRepository.observeMySavedDevices(userId)
                }
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { savedDevices ->
                    val savedIds = savedDevices.map { it.id }.toSet()
                    _uiState.update { state ->
                        state.copy(
                            mySavedDevices = savedDevices,
                            userDevices = savedDevices,
                            savedDeviceIds = savedIds
                        )
                    }
                }
        }

        // 3. Observar certificados de técnicos desde Room
        viewModelScope.launch {
            authRepository.observeAllCertificates()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
                .collect { certsByTech ->
                    if (certsByTech.isNotEmpty()) {
                        _uiState.update { it.copy(techCertificatesByAdmin = certsByTech) }
                    }
                }
        }
    }

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

    fun toggleSaveDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUser.value.id.ifBlank { Constants.DEFAULT_USER_ID }
                deviceRepository.toggleBookmark(userId, deviceId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al actualizar equipo guardado") }
            }
        }
    }

    fun selectDevice(deviceId: String) {
        if (deviceId.isNotBlank()) {
            _uiState.update { it.copy(selectedDeviceId = deviceId) }
        }
    }

    fun selectPublicTech(techId: String) {
        if (techId.isNotBlank()) {
            _uiState.update { it.copy(selectedPublicTechId = techId) }
        }
    }

    // Add Device handlers (Exclusivo para Técnico)
    fun onNewDeviceTypeSelected(type: String) {
        _uiState.update { it.copy(newDeviceType = type) }
    }

    fun onNewDeviceBrandChanged(brand: String) {
        _uiState.update {
            it.copy(
                newDeviceBrand = brand,
                isBrandError = if (brand.isNotBlank()) false else it.isBrandError,
                errorMessage = if (brand.isNotBlank() && it.newDeviceModel.isNotBlank()) null else it.errorMessage
            )
        }
    }

    fun onNewDeviceModelChanged(model: String) {
        _uiState.update {
            it.copy(
                newDeviceModel = model,
                isModelError = if (model.isNotBlank()) false else it.isModelError,
                errorMessage = if (model.isNotBlank() && it.newDeviceBrand.isNotBlank()) null else it.errorMessage
            )
        }
    }

    fun onNewDeviceImageSelected(fileName: String, imageUri: String) {
        _uiState.update {
            it.copy(
                newDeviceImageFileName = fileName,
                newDeviceImageUri = imageUri
            )
        }
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

    fun saveNewDevice(): Boolean {
        val current = _uiState.value
        val brandText = current.newDeviceBrand.trim()
        val modelText = current.newDeviceModel.trim()
        val brandEmpty = brandText.isEmpty()
        val modelEmpty = modelText.isEmpty()

        if (brandEmpty || modelEmpty) {
            _uiState.update {
                it.copy(
                    isBrandError = brandEmpty,
                    isModelError = modelEmpty,
                    errorMessage = "Por favor, ingresa el nombre y el número de modelo del equipo."
                )
            }
            return false
        }

        val normalizedCategory = if (current.newDeviceType.contains("PC", ignoreCase = true)) "PC" else "Laptop"
        val techName = authRepository.currentTech.value.fullName.ifBlank { Constants.DEFAULT_TECH_NAME }

        val newDevice = Device(
            id = "dev_${System.currentTimeMillis()}",
            name = brandText,
            model = modelText,
            type = normalizedCategory,
            technicianId = Constants.DEFAULT_TECH_ID,
            technicianName = techName,
            imageUrl = current.newDeviceImageUri,
            ram = current.newDeviceRam,
            storage = current.newDeviceStorage,
            processor = current.newDeviceProcessor
        )

        viewModelScope.launch {
            try {
                deviceRepository.insertDevice(newDevice)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al guardar el dispositivo") }
            }
        }

        _uiState.update { state ->
            state.copy(
                newDeviceBrand = "",
                newDeviceModel = "",
                newDeviceImageUri = "",
                newDeviceImageFileName = "",
                isBrandError = false,
                isModelError = false,
                errorMessage = null
            )
        }
        return true
    }

    fun removeUserDevice(deviceId: String) {
        toggleSaveDevice(deviceId)
    }

    // Diagnostic Flow handlers
    fun resetDiagnostic() {
        _uiState.update {
            it.copy(
                diagnosticStep = 1,
                selectedSymptomId = "symptom_thermal",
                selectedDetailId = "detail_load"
            )
        }
    }

    fun onSymptomSelected(symptomId: String) {
        _uiState.update { it.copy(selectedSymptomId = symptomId) }
    }

    fun onDetailSelected(detailId: String) {
        _uiState.update { it.copy(selectedDetailId = detailId) }
    }

    fun nextDiagnosticStep() {
        _uiState.update { current ->
            if (current.diagnosticStep < 3) {
                current.copy(diagnosticStep = current.diagnosticStep + 1)
            } else {
                current
            }
        }
    }

    fun previousDiagnosticStep() {
        _uiState.update { current ->
            if (current.diagnosticStep > 1) {
                current.copy(diagnosticStep = current.diagnosticStep - 1)
            } else {
                current
            }
        }
    }

    // Tech Home handlers
    fun onTechSearchQueryChanged(query: String) {
        _uiState.update { it.copy(techSearchQuery = query) }
    }

    fun onTechTabSelected(index: Int) {
        _uiState.update { it.copy(techSelectedTab = index) }
    }

    fun onTechFilterSelected(filter: String) {
        _uiState.update { it.copy(techSelectedFilter = filter) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
