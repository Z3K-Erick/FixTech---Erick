package com.fixtech.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "user_favorite_guides",
    primaryKeys = ["userId", "deviceId", "guideId"]
)
data class UserFavoriteGuideEntity(
    val userId: String,
    val deviceId: String,
    val guideId: String,
    val pinnedAt: Long
)
