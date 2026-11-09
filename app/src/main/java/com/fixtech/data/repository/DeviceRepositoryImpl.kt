package com.fixtech.data.repository

import com.fixtech.data.local.dao.DeviceDao
import com.fixtech.data.local.entity.DeviceEntity
import com.fixtech.data.local.entity.UserFavoriteDeviceEntity
import com.fixtech.data.local.entity.toDomain
import com.fixtech.data.local.entity.toEntity
import com.fixtech.data.remote.api.FixTechApiService
import com.fixtech.data.remote.dto.toDto
import com.fixtech.domain.model.Device
import com.fixtech.domain.repository.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceRepositoryImpl(
    private val deviceDao: DeviceDao,
    private val apiService: FixTechApiService? = null
) : DeviceRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            ensureSeedData()
            refreshFromRemote()
        }
    }

    override fun observeExploreDevices(currentUserId: String): Flow<List<Device>> {
        return deviceDao.observeExploreDevices(currentUserId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeMySavedDevices(currentUserId: String): Flow<List<Device>> {
        return deviceDao.observeMySavedDevices(currentUserId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeSavedDevicesCount(currentUserId: String): Flow<Int> {
        return deviceDao.observeSavedDevicesCount(currentUserId)
    }

    override fun observeTechDevicesCount(technicianId: String): Flow<Int> {
        return deviceDao.observeTechDevicesCount(technicianId)
    }

    override suspend fun existsDuplicateDevice(
        technicianId: String,
        model: String,
        type: String,
        excludeDeviceId: String?
    ): Boolean {
        return deviceDao.existsDuplicateDevice(
            technicianId = technicianId,
            model = model.trim(),
            type = type.trim(),
            excludeDeviceId = excludeDeviceId
        )
    }

    override suspend fun toggleBookmark(userId: String, deviceId: String) {
        val isCurrentlySaved = deviceDao.isDeviceBookmarked(userId, deviceId)
        if (isCurrentlySaved) {
            deviceDao.deleteFavoriteDevice(userId, deviceId)
        } else {
            deviceDao.insertFavoriteDevice(
                UserFavoriteDeviceEntity(
                    userId = userId,
                    deviceId = deviceId,
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun insertDevice(device: Device) {
        deviceDao.insertDevice(device.toEntity())
        repositoryScope.launch {
            try {
                apiService?.createDevice(device.toDto())
            } catch (_: Exception) {
                // Sincronización silenciosa en segundo plano si el servidor está apagado
            }
        }
    }

    override suspend fun deleteDeviceCascade(deviceId: String) {
        deviceDao.deleteDeviceCascade(deviceId)
    }

    override suspend fun refreshFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                val remoteDevices = apiService?.getDevices().orEmpty()
                if (remoteDevices.isNotEmpty()) {
                    val existingIds = deviceDao.getAllDeviceIds().toSet()
                    val newRemoteEntities = remoteDevices
                        .filter { it.id.isNotBlank() && it.id !in existingIds }
                        .map { it.toEntity() }
                    if (newRemoteEntities.isNotEmpty()) {
                        deviceDao.insertDevicesIgnore(newRemoteEntities)
                    }
                }
            } catch (_: Exception) {
                // Fallback offline resiliente: Room mantiene intactos los datos e imágenes locales
            }
        }
    }

    override suspend fun ensureSeedData() {
        if (deviceDao.getDevicesCount() == 0) {
            val seedDevices = listOf(
                DeviceEntity(
                    id = "dev_1",
                    name = "Lenovo LOQ",
                    model = "15IRH8",
                    type = "Laptop",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    imageUrl = "",
                    ram = "16 GB",
                    storage = "512 GB SSD",
                    processor = "Intel Core i5"
                ),
                DeviceEntity(
                    id = "dev_2",
                    name = "Corsair One",
                    model = "i300",
                    type = "PC",
                    technicianId = "admin-current",
                    technicianName = "Carlos M.",
                    imageUrl = "",
                    ram = "32 GB",
                    storage = "1 TB SSD",
                    processor = "Intel Core i9"
                ),
                DeviceEntity(
                    id = "dev_3",
                    name = "Lenovo LOQ",
                    model = "15IRH8",
                    type = "Laptop",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    imageUrl = "",
                    ram = "16 GB",
                    storage = "512 GB SSD",
                    processor = "AMD Ryzen 7"
                ),
                DeviceEntity(
                    id = "dev_4",
                    name = "Corsair One",
                    model = "i300",
                    type = "PC",
                    technicianId = "admin-other",
                    technicianName = "Ana R.",
                    imageUrl = "",
                    ram = "32 GB",
                    storage = "1 TB SSD",
                    processor = "Intel Core i7"
                )
            )
            deviceDao.insertDevices(seedDevices)
        }
    }
}
