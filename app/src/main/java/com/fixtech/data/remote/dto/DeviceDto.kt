package com.fixtech.data.remote.dto

import com.fixtech.domain.model.Device

data class DeviceDto(
    val id: String,
    val name: String,
    val model: String,
    val type: String,
    val technicianId: String,
    val technicianName: String,
    val imageUrl: String = "",
    val ram: String = "16 GB",
    val storage: String = "512 GB SSD",
    val processor: String = "Intel Core"
) {
    fun toDomain(): Device = Device(
        id = id,
        name = name,
        model = model,
        type = type,
        technicianId = technicianId,
        technicianName = technicianName,
        imageUrl = imageUrl,
        ram = ram,
        storage = storage,
        processor = processor
    )
}
