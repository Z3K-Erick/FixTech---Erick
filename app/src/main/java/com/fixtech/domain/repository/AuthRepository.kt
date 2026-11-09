package com.fixtech.domain.repository

import com.fixtech.core.common.Result
import com.fixtech.data.remote.dto.ApiMessageResponseDto
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User>
    val currentTech: StateFlow<User>
    fun observeCurrentUserId(): Flow<String>
    fun observeAllCertificates(): Flow<Map<String, List<Certificate>>>
    fun observeCertificatesForTech(technicianId: String): Flow<List<Certificate>>
    fun observeCertificatesCountForTech(technicianId: String): Flow<Int>
    suspend fun login(email: String, password: String, role: String = "USER"): Result<User>
    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String = "USER",
        verificationCode: String = ""
    ): Result<User>
    suspend fun sendVerificationCode(email: String): Result<ApiMessageResponseDto>
    suspend fun loginUser(email: String, password: String): User
    suspend fun registerUser(fullName: String, email: String, password: String): User
    suspend fun loginTech(email: String, password: String): User
    suspend fun registerTech(fullName: String, email: String, workshopCode: String, password: String): User
    suspend fun restoreSession(userId: String, name: String, role: String)
    suspend fun getUserById(userId: String): User?
    suspend fun updateUserNotifications(enabled: Boolean)
    suspend fun updateTechNotifications(enabled: Boolean)
    suspend fun updateUserAvatar(avatarUri: String?)
    suspend fun updateTechAvatar(avatarUri: String?)
    suspend fun addCertificate(
        technicianId: String,
        title: String,
        institution: String,
        fileName: String,
        localFilePath: String = "",
        mimeType: String = "application/pdf"
    )
    suspend fun deleteCertificate(certificateId: String)
    suspend fun logoutUser()
}
