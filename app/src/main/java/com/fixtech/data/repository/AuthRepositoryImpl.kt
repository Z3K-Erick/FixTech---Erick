package com.fixtech.data.repository

import com.fixtech.core.common.Constants
import com.fixtech.data.local.dao.UserDao
import com.fixtech.data.local.entity.CertificateEntity
import com.fixtech.data.local.entity.UserEntity
import com.fixtech.data.local.entity.toDomain
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.User
import com.fixtech.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow(
        User(
            id = Constants.DEFAULT_USER_ID,
            fullName = "Usuario",
            email = "",
            role = "USER",
            notificationsEnabled = true
        )
    )
    override val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _currentTech = MutableStateFlow(
        User(
            id = Constants.DEFAULT_TECH_ID,
            fullName = "Erick",
            email = "tecnico@fixtech.com",
            role = "TECH",
            notificationsEnabled = true
        )
    )
    override val currentTech: StateFlow<User> = _currentTech.asStateFlow()

    private val _currentUserId = MutableStateFlow(Constants.DEFAULT_USER_ID)

    init {
        repositoryScope.launch {
            seedCertificatesIfNeeded()
            val savedUser = userDao.getUserById(Constants.DEFAULT_USER_ID)
            if (savedUser != null) {
                _currentUser.value = savedUser.toDomain()
            }
            val savedTech = userDao.getUserById(Constants.DEFAULT_TECH_ID)
            if (savedTech != null) {
                _currentTech.value = savedTech.toDomain()
            }
        }
    }

    private suspend fun seedCertificatesIfNeeded() {
        if (userDao.getCertificatesCount() == 0) {
            val defaultCerts = listOf(
                CertificateEntity(
                    id = "cert_1",
                    technicianId = Constants.DEFAULT_TECH_ID,
                    title = "Mantenimiento de equipos de cómputo",
                    institution = "Instituto TecnoSur",
                    fileName = "mantenimiento.pdf"
                ),
                CertificateEntity(
                    id = "cert_2",
                    technicianId = Constants.DEFAULT_TECH_ID,
                    title = "Certificación en Electrónica y Microsoldadura",
                    institution = "Centro de Capacitación Técnica",
                    fileName = "electronica_cert.pdf"
                ),
                CertificateEntity(
                    id = "cert_3",
                    technicianId = Constants.SECONDARY_TECH_ID,
                    title = "Diagnóstico y Ensamble de Laptops y PC",
                    institution = "Academia Nacional de Hardware",
                    fileName = "diagnostico_hardware.pdf"
                )
            )
            userDao.insertCertificates(defaultCerts)
        }
    }

    override fun observeCurrentUserId(): Flow<String> = _currentUserId.asStateFlow()

    override fun observeAllCertificates(): Flow<Map<String, List<Certificate>>> {
        return userDao.observeAllCertificates().map { entities ->
            entities.map { it.toDomain() }.groupBy { it.technicianId }
        }
    }

    override suspend fun loginUser(email: String, password: String): User {
        val trimmedEmail = email.trim()
        val existingByEmail = userDao.getUserByEmailAndRole(trimmedEmail, "USER")
        val resolvedName = when {
            existingByEmail != null && existingByEmail.fullName.isNotBlank() -> existingByEmail.fullName
            trimmedEmail.isNotEmpty() -> trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            else -> "Usuario"
        }
        val notifEnabled = existingByEmail?.notificationsEnabled ?: _currentUser.value.notificationsEnabled
        val user = User(
            id = Constants.DEFAULT_USER_ID,
            fullName = resolvedName,
            email = trimmedEmail,
            role = "USER",
            notificationsEnabled = notifEnabled
        )
        userDao.upsertUser(
            UserEntity(
                id = Constants.DEFAULT_USER_ID,
                fullName = resolvedName,
                email = trimmedEmail,
                role = "USER",
                notificationsEnabled = notifEnabled
            )
        )
        if (trimmedEmail.isNotEmpty()) {
            userDao.upsertUser(
                UserEntity(
                    id = "user_${trimmedEmail.lowercase()}",
                    fullName = resolvedName,
                    email = trimmedEmail,
                    role = "USER",
                    notificationsEnabled = notifEnabled
                )
            )
        }
        _currentUser.value = user
        _currentUserId.value = Constants.DEFAULT_USER_ID
        return user
    }

    override suspend fun registerUser(fullName: String, email: String, password: String): User {
        val trimmedEmail = email.trim()
        val trimmedName = fullName.trim().ifEmpty {
            trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }.ifEmpty { "Usuario" }
        }
        val user = User(
            id = Constants.DEFAULT_USER_ID,
            fullName = trimmedName,
            email = trimmedEmail,
            role = "USER",
            notificationsEnabled = true
        )
        userDao.upsertUser(
            UserEntity(
                id = Constants.DEFAULT_USER_ID,
                fullName = trimmedName,
                email = trimmedEmail,
                role = "USER",
                notificationsEnabled = true
            )
        )
        if (trimmedEmail.isNotEmpty()) {
            userDao.upsertUser(
                UserEntity(
                    id = "user_${trimmedEmail.lowercase()}",
                    fullName = trimmedName,
                    email = trimmedEmail,
                    role = "USER",
                    notificationsEnabled = true
                )
            )
        }
        _currentUser.value = user
        _currentUserId.value = Constants.DEFAULT_USER_ID
        return user
    }

    override suspend fun loginTech(email: String, password: String): User {
        val trimmedEmail = email.trim()
        val existingTech = userDao.getUserByEmailAndRole(trimmedEmail, "TECH")
        val resolvedName = when {
            existingTech != null && existingTech.fullName.isNotBlank() -> existingTech.fullName
            trimmedEmail.isNotEmpty() -> trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            else -> Constants.DEFAULT_TECH_NAME
        }
        val tech = User(
            id = Constants.DEFAULT_TECH_ID,
            fullName = resolvedName,
            email = trimmedEmail,
            role = "TECH",
            notificationsEnabled = existingTech?.notificationsEnabled ?: true
        )
        userDao.upsertUser(
            UserEntity(
                id = Constants.DEFAULT_TECH_ID,
                fullName = resolvedName,
                email = trimmedEmail,
                role = "TECH",
                notificationsEnabled = tech.notificationsEnabled
            )
        )
        _currentTech.value = tech
        return tech
    }

    override suspend fun registerTech(
        fullName: String,
        email: String,
        workshopCode: String,
        password: String
    ): User {
        val trimmedEmail = email.trim()
        val trimmedName = fullName.trim().ifEmpty {
            trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }.ifEmpty { Constants.DEFAULT_TECH_NAME }
        }
        val tech = User(
            id = Constants.DEFAULT_TECH_ID,
            fullName = trimmedName,
            email = trimmedEmail,
            role = "TECH",
            workshopCode = workshopCode.trim(),
            notificationsEnabled = true
        )
        userDao.upsertUser(
            UserEntity(
                id = Constants.DEFAULT_TECH_ID,
                fullName = trimmedName,
                email = trimmedEmail,
                role = "TECH",
                workshopCode = workshopCode.trim(),
                notificationsEnabled = true
            )
        )
        if (trimmedEmail.isNotEmpty()) {
            userDao.upsertUser(
                UserEntity(
                    id = "tech_${trimmedEmail.lowercase()}",
                    fullName = trimmedName,
                    email = trimmedEmail,
                    role = "TECH",
                    workshopCode = workshopCode.trim(),
                    notificationsEnabled = true
                )
            )
        }
        _currentTech.value = tech
        return tech
    }

    override suspend fun updateUserNotifications(enabled: Boolean) {
        val updated = _currentUser.value.copy(notificationsEnabled = enabled)
        _currentUser.value = updated
        userDao.updateNotifications(Constants.DEFAULT_USER_ID, enabled)
        if (updated.email.isNotBlank()) {
            userDao.updateNotifications("user_${updated.email.trim().lowercase()}", enabled)
        }
    }

    override suspend fun updateTechNotifications(enabled: Boolean) {
        val updated = _currentTech.value.copy(notificationsEnabled = enabled)
        _currentTech.value = updated
        userDao.updateNotifications(Constants.DEFAULT_TECH_ID, enabled)
    }

    override suspend fun addCertificate(
        technicianId: String,
        title: String,
        institution: String,
        fileName: String
    ) {
        val cert = CertificateEntity(
            id = "cert_${System.currentTimeMillis()}",
            technicianId = technicianId,
            title = title,
            institution = institution,
            fileName = fileName
        )
        userDao.insertCertificate(cert)
    }

    override suspend fun logoutUser() {
        // Mantiene las tablas de Room intactas para que la persistencia local se conserve al cerrar sesión o reiniciar
    }
}
