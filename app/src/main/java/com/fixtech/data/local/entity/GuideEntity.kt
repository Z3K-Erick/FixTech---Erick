package com.fixtech.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guides")
data class GuideEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val title: String,
    val difficulty: String,
    val estimatedTime: String,
    val toolsList: String,
    val precautions: String,
    val technicianId: String,
    val technicianName: String,
    val category: String = "Laptop",
    val modelSpec: String = "",
    val imageUrl: String = ""
)

data class GuideWithFavoriteRow(
    @Embedded val guide: GuideEntity,
    val isFavorite: Boolean,
    val pinnedAt: Long?
)
