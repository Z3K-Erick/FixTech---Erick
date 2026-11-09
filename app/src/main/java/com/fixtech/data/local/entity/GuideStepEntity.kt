package com.fixtech.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_steps")
data class GuideStepEntity(
    @PrimaryKey val id: String,
    val guideId: String,
    val stepNumber: Int,
    val title: String,
    val description: String,
    val hasAlert: Boolean,
    val alertType: String,
    val alertMessage: String,
    val requiresConfirmation: Boolean,
    val confirmationText: String,
    val alertTitle: String = "Precaución",
    val imageUrl: String = "",
    val imageUri: String? = null,
    val resourceUrl: String? = null
)
