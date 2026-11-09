package com.fixtech.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val model: String,
    val type: String, // "Laptop" / "PC"
    val technicianId: String,
    val technicianName: String,
    val imageUrl: String = "",
    val ram: String = "16 GB",
    val storage: String = "512 GB SSD",
    val processor: String = "Intel Core"
)

data class DeviceWithBookmarkRow(
    @Embedded val device: DeviceEntity,
    val isBookmarked: Boolean,
    val guidesCount: Int
)
