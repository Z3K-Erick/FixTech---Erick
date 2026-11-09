package com.fixtech.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String = "USER",
    val workshopCode: String = "",
    val notificationsEnabled: Boolean = true
)
