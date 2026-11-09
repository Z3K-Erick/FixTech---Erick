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

    companion object {
        const val DUPLICATE_DEVICE_ERROR = "Ya registraste un equipo con este modelo y tipo"
    }

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
                    _uiState.update { it.copy(techCertificatesByAdmin = certsByTech) }
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

    // =========================================================================
    // Add / Edit / Delete Device handlers (Exclusivo para Técnico)
    // =========================================================================

    private fun normalizeDeviceType(type: String): String {
        return if (type.contains("PC", ignoreCase = true)) "PC" else "Laptop"
    }

    private fun isDuplicateInCatalog(
        model: String,
        type: String,
        editingDeviceId: String?,
        catalog: List<Device>
    ): Boolean {
        val normModel = model.trim().lowercase().replace(Regex("\\s+"), " ")
        if (normModel.isEmpty()) return false
        val normType = normalizeDeviceType(type)
        val techId = authRepository.currentTech.value.id.ifBlank { Constants.DEFAULT_TECH_ID }
        return catalog.any { dev ->
            dev.id != editingDeviceId &&
                dev.technicianId == techId &&
                dev.model.trim().lowercase().replace(Regex("\\s+"), " ") == normModel &&
                normalizeDeviceType(dev.type) == normType
        }
    }

    fun startNewDevice() {
        _uiState.update {
            it.copy(
                editingDeviceId = null,
                newDeviceType = "Laptop",
                newDeviceBrand = "",
                newDeviceModel = "",
                newDeviceImageUri = "",
                newDeviceImageFileName = "",
                newDeviceRam = "16 GB",
                newDeviceStorage = "512 GB SSD",
                newDeviceProcessor = "Intel Core",
                isBrandError = false,
                isModelError = false,
                isDuplicateModelError = false,
                brandErrorText = null,
                modelErrorText = null,
                errorMessage = null
            )
        }
    }

    fun startEditDevice(device: Device) {
        val normalizedType = if (device.type.contains("PC", ignoreCase = true)) "PC" else "Laptop"
        _uiState.update {
            it.copy(
                editingDeviceId = device.id,
                newDeviceType = normalizedType,
                newDeviceBrand = device.name,
                newDeviceModel = device.model,
                newDeviceImageUri = device.imageUrl,
                newDeviceImageFileName = if (device.imageUrl.isNotBlank()) "imagen_actual.jpg" else "",
                newDeviceRam = device.ram,
                newDeviceStorage = device.storage,
                newDeviceProcessor = device.processor,
                isBrandError = false,
                isModelError = false,
                isDuplicateModelError = false,
                brandErrorText = null,
                modelErrorText = null,
                errorMessage = null
            )
        }
    }

    fun onNewDeviceTypeSelected(type: String) {
        val normalized = normalizeDeviceType(type)
        _uiState.update { current ->
            val duplicate = isDuplicateInCatalog(
                model = current.newDeviceModel,
                type = normalized,
                editingDeviceId = current.editingDeviceId,
                catalog = current.catalogDevices
            )
            current.copy(
                newDeviceType = normalized,
                isDuplicateModelError = duplicate,
                isModelError = duplicate || (current.isModelError && current.newDeviceModel.isBlank()),
                modelErrorText = when {
                    duplicate -> DUPLICATE_DEVICE_ERROR
                    current.isModelError && current.newDeviceModel.isBlank() -> "Escribe el número de modelo."
                    else -> null
                },
                errorMessage = if (duplicate) DUPLICATE_DEVICE_ERROR else null
            )
        }
    }

    fun onNewDeviceBrandChanged(brand: String) {
        _uiState.update { current ->
            val notBlank = brand.trim().isNotEmpty()
            current.copy(
                newDeviceBrand = brand,
                isBrandError = if (notBlank) false else current.isBrandError,
                brandErrorText = if (notBlank) null else current.brandErrorText,
                errorMessage = if (notBlank && !current.isModelError) null else current.errorMessage
            )
        }
    }

    fun onNewDeviceModelChanged(model: String) {
        _uiState.update { current ->
            val trimmed = model.trim()
            val duplicate = isDuplicateInCatalog(
                model = trimmed,
                type = current.newDeviceType,
                editingDeviceId = current.editingDeviceId,
                catalog = current.catalogDevices
            )
            current.copy(
                newDeviceModel = model,
                isDuplicateModelError = duplicate,
                isModelError = duplicate || (current.isModelError && trimmed.isEmpty()),
                modelErrorText = when {
                    duplicate -> DUPLICATE_DEVICE_ERROR
                    current.isModelError && trimmed.isEmpty() -> "Escribe el número de modelo."
                    else -> null
                },
                errorMessage = if (duplicate) DUPLICATE_DEVICE_ERROR else if (trimmed.isNotEmpty() && !current.isBrandError) null else current.errorMessage
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

    fun saveDevice(onSuccess: () -> Unit = {}) {
        val current = _uiState.value
        val brandText = current.newDeviceBrand.trim()
        val modelText = current.newDeviceModel.trim()
        val brandEmpty = brandText.isEmpty()
        val modelEmpty = modelText.isEmpty()

        if (brandEmpty || modelEmpty) {
            _uiState.update {
                it.copy(
                    isBrandError = brandEmpty,
                    brandErrorText = if (brandEmpty) "Escribe el nombre del equipo." else null,
                    isModelError = modelEmpty,
                    modelErrorText = if (modelEmpty) "Escribe el número de modelo." else null,
                    errorMessage = "Falta información obligatoria: ingresa el nombre y el modelo del equipo."
                )
            }
            return
        }

        val normalizedCategory = normalizeDeviceType(current.newDeviceType)
        val techId = authRepository.currentTech.value.id.ifBlank { Constants.DEFAULT_TECH_ID }
        val techName = authRepository.currentTech.value.fullName.ifBlank { Constants.DEFAULT_TECH_NAME }

        // Validación en memoria + verificación en Room para duplicados (mismo técnico, mismo model y type)
        val memoryDuplicate = isDuplicateInCatalog(
            model = modelText,
            type = normalizedCategory,
            editingDeviceId = current.editingDeviceId,
            catalog = current.catalogDevices
        )

        if (memoryDuplicate) {
            _uiState.update {
                it.copy(
                    isModelError = true,
                    isDuplicateModelError = true,
                    modelErrorText = DUPLICATE_DEVICE_ERROR,
                    errorMessage = DUPLICATE_DEVICE_ERROR
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val roomDuplicate = deviceRepository.existsDuplicateDevice(
                    technicianId = techId,
                    model = modelText,
                    type = normalizedCategory,
                    excludeDeviceId = current.editingDeviceId
                )
                if (roomDuplicate) {
                    _uiState.update {
                        it.copy(
                            isModelError = true,
                            isDuplicateModelError = true,
                            modelErrorText = DUPLICATE_DEVICE_ERROR,
                            errorMessage = DUPLICATE_DEVICE_ERROR
                        )
                    }
                    return@launch
                }

                val deviceId = current.editingDeviceId ?: "dev_${System.currentTimeMillis()}"
                val deviceToSave = Device(
                    id = deviceId,
                    name = brandText,
                    model = modelText,
                    type = normalizedCategory,
                    technicianId = techId,
                    technicianName = techName,
                    imageUrl = current.newDeviceImageUri,
                    ram = current.newDeviceRam,
                    storage = current.newDeviceStorage,
                    processor = current.newDeviceProcessor
                )

                deviceRepository.insertDevice(deviceToSave)

                _uiState.update { state ->
                    state.copy(
                        editingDeviceId = null,
                        newDeviceBrand = "",
                        newDeviceModel = "",
                        newDeviceImageUri = "",
                        newDeviceImageFileName = "",
                        isBrandError = false,
                        isModelError = false,
                        isDuplicateModelError = false,
                        brandErrorText = null,
                        modelErrorText = null,
                        errorMessage = null
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al guardar el dispositivo") }
            }
        }
    }

    fun deleteTechDevice(deviceId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                deviceRepository.deleteDeviceCascade(deviceId)
                _uiState.update { state ->
                    if (state.expandedTechDeviceId == deviceId) {
                        state.copy(expandedTechDeviceId = null)
                    } else {
                        state
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al eliminar el dispositivo") }
            }
        }
    }

    fun toggleExpandTechDevice(deviceId: String) {
        _uiState.update { current ->
            val nextId = if (current.expandedTechDeviceId == deviceId) null else deviceId
            current.copy(expandedTechDeviceId = nextId)
        }
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
