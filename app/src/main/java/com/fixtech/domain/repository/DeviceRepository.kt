package com.fixtech.domain.repository

import com.fixtech.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun observeExploreDevices(currentUserId: String): Flow<List<Device>>
    fun observeMySavedDevices(currentUserId: String): Flow<List<Device>>
    fun observeSavedDevicesCount(currentUserId: String): Flow<Int>
    fun observeTechDevicesCount(technicianId: String): Flow<Int>
    suspend fun existsDuplicateDevice(
        technicianId: String,
        model: String,
        type: String,
        excludeDeviceId: String? = null
    ): Boolean
    suspend fun toggleBookmark(userId: String, deviceId: String)
    suspend fun insertDevice(device: Device)
    suspend fun deleteDeviceCascade(deviceId: String)
    suspend fun ensureSeedData()
    suspend fun refreshFromRemote()
}
