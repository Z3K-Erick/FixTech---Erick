package com.fixtech.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "user_favorite_devices",
    primaryKeys = ["userId", "deviceId"]
)
data class UserFavoriteDeviceEntity(
    val userId: String,
    val deviceId: String,
    val savedAt: Long
)
