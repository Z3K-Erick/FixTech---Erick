package com.fixtech.data.remote.dto

import com.fixtech.domain.model.User

data class UserDto(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String = "USER",
    val workshopCode: String = ""
) {
    fun toDomain(): User = User(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        workshopCode = workshopCode
    )
}
