package com.fixtech.data.remote.api

import com.fixtech.data.remote.dto.DeviceDto
import com.fixtech.data.remote.dto.GuideDto
import com.fixtech.data.remote.dto.UserDto

interface FixTechApiService {
    suspend fun fetchCatalogDevices(): List<DeviceDto>
    suspend fun fetchGuides(): List<GuideDto>
    suspend fun authenticateUser(email: String, password: String): UserDto
}
