package com.fixtech.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val role: String = "USER",
    val workshopCode: String = "",
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val id: String,
    val technicianId: String,
    val title: String,
    val institution: String,
    val fileName: String,
    val localFilePath: String = "",
    val mimeType: String = "application/pdf"
)
