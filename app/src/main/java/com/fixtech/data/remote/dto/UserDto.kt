package com.fixtech.data.remote.dto

import com.fixtech.data.local.entity.UserEntity
import com.fixtech.domain.model.User

data class UserDto(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "USER",
    val workshopCode: String = ""
) {
    fun toDomain(
        notificationsEnabled: Boolean = true,
        avatarUri: String? = null
    ): User = User(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        workshopCode = workshopCode,
        notificationsEnabled = notificationsEnabled,
        avatarUri = avatarUri
    )

    fun toEntity(
        notificationsEnabled: Boolean = true,
        avatarUri: String? = null
    ): UserEntity = UserEntity(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        workshopCode = workshopCode,
        notificationsEnabled = notificationsEnabled,
        avatarUri = avatarUri
    )
}

fun User.toDto(): UserDto = UserDto(
    id = id,
    fullName = fullName,
    email = email,
    role = role,
    workshopCode = workshopCode
)

data class LoginRequestDto(
    val email: String,
    val password: String,
    val role: String = "USER"
)

data class SendCodeRequestDto(
    val email: String
)

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "USER",
    val verificationCode: String = ""
)

data class AuthResponseDto(
    val success: Boolean,
    val message: String,
    val user: UserDto? = null
)

data class ApiMessageResponseDto(
    val success: Boolean,
    val message: String,
    val demoCode: String? = "123456"
)
