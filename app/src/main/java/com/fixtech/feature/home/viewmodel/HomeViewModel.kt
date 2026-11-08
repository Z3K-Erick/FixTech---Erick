package com.fixtech.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import com.fixtech.feature.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val sharedHomeState = MutableStateFlow(HomeUiState())
private val savedDeviceIdsByUser = mutableMapOf<String, Set<String>>()
private var activeHomeUserKey: String = "default_user"

class HomeViewModel : ViewModel() {

    val uiState: StateFlow<HomeUiState> = sharedHomeState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        sharedHomeState.update { it.copy(searchQuery = query) }
    }

    fun onDeviceSearchQueryChanged(query: String) {
        sharedHomeState.update { it.copy(deviceSearchQuery = query) }
    }

    fun onExploreSearchQueryChanged(query: String) {
        sharedHomeState.update { it.copy(exploreSearchQuery = query) }
    }

    fun onExploreCategorySelected(category: String?) {
        sharedHomeState.update { it.copy(exploreSelectedCategory = category) }
    }

    fun onTabSelected(index: Int) {
        sharedHomeState.update { it.copy(selectedTab = index) }
    }

    fun toggleSaveDevice(deviceId: String) {
        sharedHomeState.update { state ->
            val currentSaved = state.savedDeviceIds
            val updatedSaved = if (deviceId in currentSaved) {
                currentSaved - deviceId
            } else {
                currentSaved + deviceId
            }
            savedDeviceIdsByUser[activeHomeUserKey] = updatedSaved
            val validSavedDevices = state.catalogDevices.filter { it.id in updatedSaved }
            ProfileViewModel.updateSavedDevicesCount(validSavedDevices.size)
            state.copy(
                savedDeviceIds = updatedSaved,
                userDevices = validSavedDevices
            )
        }
    }

    fun selectDevice(deviceId: String) {
        sharedHomeState.update { it.copy(selectedDeviceId = deviceId) }
    }

    fun selectPublicTech(techId: String) {
        sharedHomeState.update { it.copy(selectedPublicTechId = techId) }
    }

    // Add Device handlers (Exclusivo para Técnico)
    fun onNewDeviceTypeSelected(type: String) {
        sharedHomeState.update { it.copy(newDeviceType = type) }
    }

    fun onNewDeviceBrandChanged(brand: String) {
        sharedHomeState.update {
            it.copy(
                newDeviceBrand = brand,
                isBrandError = if (brand.isNotBlank()) false else it.isBrandError
            )
        }
    }

    fun onNewDeviceModelChanged(model: String) {
        sharedHomeState.update {
            it.copy(
                newDeviceModel = model,
                isModelError = if (model.isNotBlank()) false else it.isModelError
            )
        }
    }

    fun onNewDeviceRamSelected(ram: String) {
        sharedHomeState.update { it.copy(newDeviceRam = ram) }
    }

    fun onNewDeviceStorageSelected(storage: String) {
        sharedHomeState.update { it.copy(newDeviceStorage = storage) }
    }

    fun onNewDeviceProcessorSelected(processor: String) {
        sharedHomeState.update { it.copy(newDeviceProcessor = processor) }
    }

    fun saveNewDevice(): Boolean {
        val current = sharedHomeState.value
        val brandText = current.newDeviceBrand.trim().ifEmpty { "Equipo ${current.newDeviceType}" }
        val modelText = current.newDeviceModel.trim().ifEmpty { "Serie Estándar" }
        val normalizedCategory = if (current.newDeviceType.contains("PC", ignoreCase = true)) "PC" else "Laptop"

        val newDevice = UserDeviceItem(
            id = "dev_${System.currentTimeMillis()}",
            name = brandText,
            model = modelText,
            category = normalizedCategory,
            authorId = "admin-current",
            authorName = ProfileViewModel.getActiveTechName(),
            ram = current.newDeviceRam,
            storage = current.newDeviceStorage,
            processor = current.newDeviceProcessor
        )

        sharedHomeState.update { state ->
            val updatedCatalog = state.catalogDevices + newDevice
            state.copy(
                catalogDevices = updatedCatalog,
                newDeviceBrand = "",
                newDeviceModel = "",
                isBrandError = false,
                isModelError = false
            )
        }
        return true
    }

    fun removeUserDevice(deviceId: String) {
        toggleSaveDevice(deviceId)
    }

    // Diagnostic Flow handlers
    fun resetDiagnostic() {
        sharedHomeState.update {
            it.copy(
                diagnosticStep = 1,
                selectedSymptomId = "symptom_thermal",
                selectedDetailId = "detail_load"
            )
        }
    }

    fun onSymptomSelected(symptomId: String) {
        sharedHomeState.update { it.copy(selectedSymptomId = symptomId) }
    }

    fun onDetailSelected(detailId: String) {
        sharedHomeState.update { it.copy(selectedDetailId = detailId) }
    }

    fun nextDiagnosticStep() {
        sharedHomeState.update { current ->
            if (current.diagnosticStep < 3) {
                current.copy(diagnosticStep = current.diagnosticStep + 1)
            } else {
                current
            }
        }
    }

    fun previousDiagnosticStep() {
        sharedHomeState.update { current ->
            if (current.diagnosticStep > 1) {
                current.copy(diagnosticStep = current.diagnosticStep - 1)
            } else {
                current
            }
        }
    }

    // Tech Home handlers
    fun onTechSearchQueryChanged(query: String) {
        sharedHomeState.update { it.copy(techSearchQuery = query) }
    }

    fun onTechTabSelected(index: Int) {
        sharedHomeState.update { it.copy(techSelectedTab = index) }
    }

    fun onTechFilterSelected(filter: String) {
        sharedHomeState.update { it.copy(techSelectedFilter = filter) }
    }

    companion object {
        fun switchUserSession(email: String) {
            val key = email.trim().lowercase().ifEmpty { "default_user" }
            activeHomeUserKey = key
            val savedIds = savedDeviceIdsByUser[key] ?: emptySet()
            sharedHomeState.update { state ->
                val validSavedDevices = state.catalogDevices.filter { it.id in savedIds }
                ProfileViewModel.updateSavedDevicesCount(validSavedDevices.size)
                state.copy(
                    savedDeviceIds = savedIds,
                    userDevices = validSavedDevices,
                    selectedTab = 0,
                    deviceSearchQuery = "",
                    exploreSearchQuery = ""
                )
            }
        }

        fun clearUserSession() {
            activeHomeUserKey = "logged_out"
            sharedHomeState.update { state ->
                ProfileViewModel.updateSavedDevicesCount(0)
                state.copy(
                    savedDeviceIds = emptySet(),
                    userDevices = emptyList(),
                    selectedTab = 0,
                    deviceSearchQuery = "",
                    exploreSearchQuery = ""
                )
            }
        }

        fun addTechCertificate(title: String, institution: String, fileName: String) {
            sharedHomeState.update { state ->
                val currentCerts = state.techCertificatesByAdmin["admin-current"] ?: emptyList()
                val newCert = PublicCertificateItem(
                    id = "cert_${System.currentTimeMillis()}",
                    title = title,
                    institution = institution,
                    fileName = fileName
                )
                val updatedMap = state.techCertificatesByAdmin.toMutableMap()
                updatedMap["admin-current"] = currentCerts + newCert
                state.copy(techCertificatesByAdmin = updatedMap)
            }
        }
    }
}
