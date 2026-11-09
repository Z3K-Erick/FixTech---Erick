package com.fixtech.data.repository

import com.fixtech.core.common.Constants
import com.fixtech.core.common.Resource
import com.fixtech.core.common.Result
import com.fixtech.data.local.SessionManager
import com.fixtech.data.local.dao.UserDao
import com.fixtech.data.local.entity.CertificateEntity
import com.fixtech.data.local.entity.UserEntity
import com.fixtech.data.local.entity.toDomain
import com.fixtech.data.local.entity.toEntity
import com.fixtech.data.remote.api.FixTechApiService
import com.fixtech.data.remote.dto.ApiMessageResponseDto
import com.fixtech.data.remote.dto.LoginRequestDto
import com.fixtech.data.remote.dto.RegisterRequestDto
import com.fixtech.data.remote.dto.SendCodeRequestDto
import com.fixtech.data.remote.dto.UserDto
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.User
import com.fixtech.domain.repository.AuthRepository
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager? = null,
    private val apiService: FixTechApiService? = null
) : AuthRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow(
        User(
            id = Constants.DEFAULT_USER_ID,
            fullName = "Usuario",
            email = "",
            role = "USER",
            notificationsEnabled = true,
            avatarUri = null
        )
    )
    override val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _currentTech = MutableStateFlow(
        User(
            id = Constants.DEFAULT_TECH_ID,
            fullName = Constants.DEFAULT_TECH_NAME,
            email = "carlos@fixtech.com",
            role = "TECH",
            notificationsEnabled = true,
            avatarUri = null
        )
    )
    override val currentTech: StateFlow<User> = _currentTech.asStateFlow()

    private val _currentUserId = MutableStateFlow(Constants.DEFAULT_USER_ID)

    init {
        repositoryScope.launch {
            val savedUser = userDao.getUserById(Constants.DEFAULT_USER_ID)
            if (savedUser != null) {
                _currentUser.value = savedUser.toDomain()
                _currentUserId.value = savedUser.id
            }
            val savedTech = userDao.getUserById(Constants.DEFAULT_TECH_ID)
            if (savedTech != null) {
                _currentTech.value = savedTech.toDomain()
            } else {
                userDao.upsertUser(
                    UserEntity(
                        id = Constants.DEFAULT_TECH_ID,
                        fullName = Constants.DEFAULT_TECH_NAME,
                        email = "carlos@fixtech.com",
                        role = "TECH",
                        notificationsEnabled = true,
                        avatarUri = null
                    )
                )
            }

            // Si existe una sesión activa en DataStore, restauramos el usuario/técnico activo en memoria
            val activeSession = sessionManager?.sessionFlow?.firstOrNull()
            if (activeSession != null && activeSession.isLoggedIn) {
                restoreSession(
                    userId = activeSession.userId,
                    name = activeSession.userName,
                    role = activeSession.role
                )
            }
        }
    }

    override fun observeCurrentUserId(): Flow<String> = _currentUserId.asStateFlow()

    override fun observeAllCertificates(): Flow<Map<String, List<Certificate>>> {
        return userDao.observeAllCertificates().map { entities ->
            entities.map { it.toDomain() }.groupBy { it.technicianId }
        }
    }

    override fun observeCertificatesForTech(technicianId: String): Flow<List<Certificate>> {
        return userDao.observeCertificatesForTech(technicianId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeCertificatesCountForTech(technicianId: String): Flow<Int> {
        return userDao.observeCertificatesCountForTech(technicianId)
    }

    override suspend fun login(email: String, password: String, role: String): Result<User> =
        withContext(Dispatchers.IO) {
            val normalizedRole = if (role.equals("TECH", ignoreCase = true)) "TECH" else "USER"
            val trimmedEmail = email.trim()
            val api = apiService
            if (api == null) {
                val localUser = if (normalizedRole == "TECH") {
                    loginTechLocal(trimmedEmail, password)
                } else {
                    loginUserLocal(trimmedEmail, password)
                }
                return@withContext Resource.Success(localUser)
            }

            try {
                val response = api.login(
                    LoginRequestDto(
                        email = trimmedEmail,
                        password = password,
                        role = normalizedRole
                    )
                )
                if (response.success) {
                    val savedUser = persistRemoteUserAndSession(
                        remoteUser = response.user,
                        fallbackEmail = trimmedEmail,
                        fallbackName = response.user?.fullName.orEmpty(),
                        fallbackRole = normalizedRole,
                        fallbackWorkshopCode = response.user?.workshopCode.orEmpty()
                    )
                    Resource.Success(savedUser)
                } else {
                    Resource.Error(response.message.ifBlank { "Credenciales incorrectas" })
                }
            } catch (e: HttpException) {
                Resource.Error("Credenciales incorrectas", e)
            } catch (e: ConnectException) {
                // Fallback Resiliente (Modo Offline/Demo)
                val localUser = if (normalizedRole == "TECH") {
                    loginTechLocal(trimmedEmail, password)
                } else {
                    loginUserLocal(trimmedEmail, password)
                }
                Resource.Success(localUser)
            } catch (e: SocketTimeoutException) {
                // Fallback Resiliente (Modo Offline/Demo)
                val localUser = if (normalizedRole == "TECH") {
                    loginTechLocal(trimmedEmail, password)
                } else {
                    loginUserLocal(trimmedEmail, password)
                }
                Resource.Success(localUser)
            } catch (e: IOException) {
                // Fallback Resiliente (Modo Offline/Demo)
                val localUser = if (normalizedRole == "TECH") {
                    loginTechLocal(trimmedEmail, password)
                } else {
                    loginUserLocal(trimmedEmail, password)
                }
                Resource.Success(localUser)
            } catch (e: Exception) {
                val localUser = if (normalizedRole == "TECH") {
                    loginTechLocal(trimmedEmail, password)
                } else {
                    loginUserLocal(trimmedEmail, password)
                }
                Resource.Success(localUser)
            }
        }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        verificationCode: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val normalizedRole = if (role.equals("TECH", ignoreCase = true)) "TECH" else "USER"
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedCode = verificationCode.trim()
        val api = apiService

        if (api == null) {
            val localUser = if (normalizedRole == "TECH") {
                registerTechLocal(trimmedName, trimmedEmail, trimmedCode, password)
            } else {
                registerUserLocal(trimmedName, trimmedEmail, password)
            }
            return@withContext Resource.Success(localUser)
        }

        try {
            val response = api.register(
                RegisterRequestDto(
                    name = trimmedName,
                    email = trimmedEmail,
                    password = password,
                    role = normalizedRole,
                    verificationCode = trimmedCode
                )
            )
            if (response.success) {
                val savedUser = persistRemoteUserAndSession(
                    remoteUser = response.user,
                    fallbackEmail = trimmedEmail,
                    fallbackName = trimmedName,
                    fallbackRole = normalizedRole,
                    fallbackWorkshopCode = trimmedCode
                )
                Resource.Success(savedUser)
            } else {
                Resource.Error(response.message.ifBlank { "No se pudo registrar la cuenta" })
            }
        } catch (e: HttpException) {
            Resource.Error("Datos de registro inválidos", e)
        } catch (e: ConnectException) {
            // Fallback Resiliente (Modo Offline/Demo)
            val localUser = if (normalizedRole == "TECH") {
                registerTechLocal(trimmedName, trimmedEmail, trimmedCode, password)
            } else {
                registerUserLocal(trimmedName, trimmedEmail, password)
            }
            Resource.Success(localUser)
        } catch (e: SocketTimeoutException) {
            // Fallback Resiliente (Modo Offline/Demo)
            val localUser = if (normalizedRole == "TECH") {
                registerTechLocal(trimmedName, trimmedEmail, trimmedCode, password)
            } else {
                registerUserLocal(trimmedName, trimmedEmail, password)
            }
            Resource.Success(localUser)
        } catch (e: IOException) {
            // Fallback Resiliente (Modo Offline/Demo)
            val localUser = if (normalizedRole == "TECH") {
                registerTechLocal(trimmedName, trimmedEmail, trimmedCode, password)
            } else {
                registerUserLocal(trimmedName, trimmedEmail, password)
            }
            Resource.Success(localUser)
        } catch (e: Exception) {
            val localUser = if (normalizedRole == "TECH") {
                registerTechLocal(trimmedName, trimmedEmail, trimmedCode, password)
            } else {
                registerUserLocal(trimmedName, trimmedEmail, password)
            }
            Resource.Success(localUser)
        }
    }

    override suspend fun sendVerificationCode(email: String): Result<ApiMessageResponseDto> =
        withContext(Dispatchers.IO) {
            val trimmedEmail = email.trim()
            if (trimmedEmail.isBlank()) {
                return@withContext Resource.Error("Ingresa tu correo electrónico para enviar el código")
            }

            val api = apiService
            if (api == null) {
                return@withContext Resource.Success(
                    ApiMessageResponseDto(
                        success = true,
                        message = "Código de confirmación enviado a $trimmedEmail",
                        demoCode = "123456"
                    )
                )
            }

            try {
                val response = api.sendVerificationCode(SendCodeRequestDto(email = trimmedEmail))
                if (response.success) {
                    Resource.Success(response)
                } else {
                    Resource.Error(response.message.ifBlank { "No se pudo enviar el código" })
                }
            } catch (e: Exception) {
                // Fallback Resiliente (Modo Offline/Demo)
                Resource.Success(
                    ApiMessageResponseDto(
                        success = true,
                        message = "Código de confirmación enviado a $trimmedEmail",
                        demoCode = "123456"
                    )
                )
            }
        }

    private suspend fun persistRemoteUserAndSession(
        remoteUser: UserDto?,
        fallbackEmail: String,
        fallbackName: String,
        fallbackRole: String,
        fallbackWorkshopCode: String
    ): User {
        val role = remoteUser?.role?.ifBlank { fallbackRole } ?: fallbackRole
        val email = remoteUser?.email?.ifBlank { fallbackEmail } ?: fallbackEmail
        val name = remoteUser?.fullName?.ifBlank { fallbackName } ?: fallbackName
        val workshopCode = remoteUser?.workshopCode?.ifBlank { fallbackWorkshopCode } ?: fallbackWorkshopCode

        return if (role.equals("TECH", ignoreCase = true)) {
            registerOrUpdateTechLocal(
                fullName = name,
                email = email,
                workshopCode = workshopCode
            )
        } else {
            registerOrUpdateUserLocal(
                fullName = name,
                email = email
            )
        }
    }

    override suspend fun loginUser(email: String, password: String): User {
        return when (val result = login(email, password, "USER")) {
            is Resource.Success -> result.data
            else -> loginUserLocal(email, password)
        }
    }

    override suspend fun registerUser(fullName: String, email: String, password: String): User {
        return when (val result = register(fullName, email, password, "USER")) {
            is Resource.Success -> result.data
            else -> registerUserLocal(fullName, email, password)
        }
    }

    override suspend fun loginTech(email: String, password: String): User {
        return when (val result = login(email, password, "TECH")) {
            is Resource.Success -> result.data
            else -> loginTechLocal(email, password)
        }
    }

    override suspend fun registerTech(
        fullName: String,
        email: String,
        workshopCode: String,
        password: String
    ): User {
        return when (val result = register(fullName, email, password, "TECH", workshopCode)) {
            is Resource.Success -> result.data
            else -> registerTechLocal(fullName, email, workshopCode, password)
        }
    }

    private suspend fun loginUserLocal(email: String, password: String): User {
        val trimmedEmail = email.trim()
        val accountUserId = if (trimmedEmail.isNotEmpty()) {
            "user_${trimmedEmail.lowercase()}"
        } else {
            Constants.DEFAULT_USER_ID
        }
        val existingByEmail = userDao.getUserById(accountUserId)
            ?: userDao.getUserByEmailAndRole(trimmedEmail, "USER")
            ?: userDao.getUserById(trimmedEmail.lowercase())

        val resolvedId = existingByEmail?.id ?: accountUserId
        val resolvedName = when {
            existingByEmail != null && existingByEmail.fullName.isNotBlank() -> existingByEmail.fullName
            trimmedEmail.isNotEmpty() -> trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            else -> "Usuario"
        }
        val notifEnabled = existingByEmail?.notificationsEnabled ?: true
        val avatarUri = existingByEmail?.avatarUri

        val user = User(
            id = resolvedId,
            fullName = resolvedName,
            email = trimmedEmail,
            role = "USER",
            notificationsEnabled = notifEnabled,
            avatarUri = avatarUri
        )

        userDao.upsertUser(user.toEntity())
        userDao.upsertUser(
            UserEntity(
                id = Constants.DEFAULT_USER_ID,
                fullName = resolvedName,
                email = trimmedEmail,
                role = "USER",
                notificationsEnabled = notifEnabled,
                avatarUri = avatarUri
            )
        )

        _currentUser.value = user
        _currentUserId.value = resolvedId
        sessionManager?.saveSession(userId = resolvedId, name = resolvedName, role = "USER")
        return user
    }

    private suspend fun registerOrUpdateUserLocal(fullName: String, email: String): User {
        return registerUserLocal(fullName, email, "")
    }

    private suspend fun registerUserLocal(fullName: String, email: String, password: String): User {
        val trimmedEmail = email.trim()
        val accountUserId = if (trimmedEmail.isNotEmpty()) {
            "user_${trimmedEmail.lowercase()}"
        } else {
            Constants.DEFAULT_USER_ID
        }
        val existing = userDao.getUserById(accountUserId)
            ?: userDao.getUserByEmailAndRole(trimmedEmail, "USER")

        val resolvedId = existing?.id ?: accountUserId
        val trimmedName = fullName.trim().ifEmpty {
            existing?.fullName?.takeIf { it.isNotBlank() }
                ?: trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }.ifEmpty { "Usuario" }
        }
        val notifEnabled = existing?.notificationsEnabled ?: true
        val avatarUri = existing?.avatarUri

        val user = User(
            id = resolvedId,
            fullName = trimmedName,
            email = trimmedEmail,
            role = "USER",
            notificationsEnabled = notifEnabled,
            avatarUri = avatarUri
        )

        userDao.upsertUser(user.toEntity())
        userDao.upsertUser(
            UserEntity(
                id = Constants.DEFAULT_USER_ID,
                fullName = trimmedName,
                email = trimmedEmail,
                role = "USER",
                notificationsEnabled = notifEnabled,
                avatarUri = avatarUri
            )
        )

        _currentUser.value = user
        _currentUserId.value = resolvedId
        sessionManager?.saveSession(userId = resolvedId, name = trimmedName, role = "USER")
        return user
    }

    private suspend fun loginTechLocal(email: String, password: String): User {
        val trimmedEmail = email.trim()
        val techAccountId = if (trimmedEmail.isNotEmpty()) {
            "tech_${trimmedEmail.lowercase()}"
        } else {
            Constants.DEFAULT_TECH_ID
        }
        val existingTech = userDao.getUserById(techAccountId)
            ?: userDao.getUserByEmailAndRole(trimmedEmail, "TECH")
            ?: userDao.getUserById(Constants.DEFAULT_TECH_ID)

        val matchesEmail = existingTech != null &&
            (trimmedEmail.isEmpty() || existingTech.email.equals(trimmedEmail, ignoreCase = true))

        val resolvedName = when {
            matchesEmail && existingTech!!.fullName.isNotBlank() -> existingTech.fullName
            trimmedEmail.isNotEmpty() -> trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            existingTech != null && existingTech.fullName.isNotBlank() -> existingTech.fullName
            else -> Constants.DEFAULT_TECH_NAME
        }
        val resolvedAvatar = if (matchesEmail) existingTech?.avatarUri else _currentTech.value.avatarUri
        val resolvedNotif = existingTech?.notificationsEnabled ?: true
        val resolvedWorkshop = existingTech?.workshopCode.orEmpty()

        val tech = User(
            id = Constants.DEFAULT_TECH_ID,
            fullName = resolvedName,
            email = trimmedEmail.ifBlank { existingTech?.email ?: "carlos@fixtech.com" },
            role = "TECH",
            workshopCode = resolvedWorkshop,
            notificationsEnabled = resolvedNotif,
            avatarUri = resolvedAvatar
        )

        userDao.upsertUser(tech.toEntity())
        if (trimmedEmail.isNotEmpty()) {
            userDao.upsertUser(tech.copy(id = techAccountId).toEntity())
        }

        _currentTech.value = tech
        sessionManager?.saveSession(
            userId = techAccountId,
            name = resolvedName,
            role = "TECH"
        )
        return tech
    }

    private suspend fun registerOrUpdateTechLocal(
        fullName: String,
        email: String,
        workshopCode: String
    ): User {
        return registerTechLocal(fullName, email, workshopCode, "")
    }

    private suspend fun registerTechLocal(
        fullName: String,
        email: String,
        workshopCode: String,
        password: String
    ): User {
        val trimmedEmail = email.trim()
        val techAccountId = if (trimmedEmail.isNotEmpty()) {
            "tech_${trimmedEmail.lowercase()}"
        } else {
            Constants.DEFAULT_TECH_ID
        }
        val existingTech = userDao.getUserById(techAccountId)
            ?: userDao.getUserByEmailAndRole(trimmedEmail, "TECH")
            ?: userDao.getUserById(Constants.DEFAULT_TECH_ID)

        val matchesEmail = existingTech != null &&
            (trimmedEmail.isEmpty() || existingTech.email.equals(trimmedEmail, ignoreCase = true))

        val trimmedName = fullName.trim().ifEmpty {
            if (matchesEmail && existingTech!!.fullName.isNotBlank()) {
                existingTech.fullName
            } else {
                trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }.ifEmpty { Constants.DEFAULT_TECH_NAME }
            }
        }

        val resolvedAvatar = if (matchesEmail) existingTech?.avatarUri else _currentTech.value.avatarUri

        val tech = User(
            id = Constants.DEFAULT_TECH_ID,
            fullName = trimmedName,
            email = trimmedEmail.ifBlank { existingTech?.email ?: "carlos@fixtech.com" },
            role = "TECH",
            workshopCode = workshopCode.trim().ifEmpty { existingTech?.workshopCode.orEmpty() },
            notificationsEnabled = existingTech?.notificationsEnabled ?: true,
            avatarUri = resolvedAvatar
        )

        userDao.upsertUser(tech.toEntity())
        if (trimmedEmail.isNotEmpty()) {
            userDao.upsertUser(tech.copy(id = techAccountId).toEntity())
        }

        _currentTech.value = tech
        sessionManager?.saveSession(
            userId = techAccountId,
            name = trimmedName,
            role = "TECH"
        )
        return tech
    }

    override suspend fun restoreSession(userId: String, name: String, role: String) {
        if (role.equals("TECH", ignoreCase = true)) {
            val existingTech = userDao.getUserById(userId)
                ?: userDao.getUserByEmailAndRole(userId.removePrefix("tech_"), "TECH")
                ?: userDao.getUserById(Constants.DEFAULT_TECH_ID)

            val resolvedName = existingTech?.fullName?.takeIf { it.isNotBlank() }
                ?: name.ifBlank { Constants.DEFAULT_TECH_NAME }
            val resolvedEmail = existingTech?.email?.takeIf { it.isNotBlank() }
                ?: userId.removePrefix("tech_").ifBlank { "carlos@fixtech.com" }

            val tech = User(
                id = Constants.DEFAULT_TECH_ID,
                fullName = resolvedName,
                email = resolvedEmail,
                role = "TECH",
                workshopCode = existingTech?.workshopCode.orEmpty(),
                notificationsEnabled = existingTech?.notificationsEnabled ?: true,
                avatarUri = existingTech?.avatarUri
            )
            userDao.upsertUser(tech.toEntity())
            _currentTech.value = tech
        } else {
            val existingUser = userDao.getUserById(userId)
                ?: userDao.getUserByEmailAndRole(userId.removePrefix("user_"), "USER")
                ?: userDao.getUserById(Constants.DEFAULT_USER_ID)

            val resolvedId = existingUser?.id ?: userId.ifBlank { Constants.DEFAULT_USER_ID }
            val resolvedName = existingUser?.fullName?.takeIf { it.isNotBlank() }
                ?: name.ifBlank { "Usuario" }
            val resolvedEmail = existingUser?.email?.takeIf { it.isNotBlank() }
                ?: userId.removePrefix("user_")

            val user = User(
                id = resolvedId,
                fullName = resolvedName,
                email = resolvedEmail,
                role = "USER",
                notificationsEnabled = existingUser?.notificationsEnabled ?: true,
                avatarUri = existingUser?.avatarUri
            )
            userDao.upsertUser(user.toEntity())
            _currentUser.value = user
            _currentUserId.value = resolvedId
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)?.toDomain()
    }

    override suspend fun updateUserNotifications(enabled: Boolean) {
        val updated = _currentUser.value.copy(notificationsEnabled = enabled)
        _currentUser.value = updated
        userDao.updateNotifications(updated.id, enabled)
        userDao.updateNotifications(Constants.DEFAULT_USER_ID, enabled)
        if (updated.email.isNotBlank()) {
            userDao.updateNotifications("user_${updated.email.trim().lowercase()}", enabled)
        }
    }

    override suspend fun updateTechNotifications(enabled: Boolean) {
        val updated = _currentTech.value.copy(notificationsEnabled = enabled)
        _currentTech.value = updated
        userDao.updateNotifications(Constants.DEFAULT_TECH_ID, enabled)
        if (updated.email.isNotBlank()) {
            userDao.updateNotifications("tech_${updated.email.trim().lowercase()}", enabled)
        }
    }

    override suspend fun updateUserAvatar(avatarUri: String?) {
        val current = _currentUser.value
        val updated = current.copy(avatarUri = avatarUri)
        _currentUser.value = updated
        userDao.upsertUser(updated.toEntity())
        userDao.updateUserAvatar(current.id, avatarUri)
        userDao.updateUserAvatar(Constants.DEFAULT_USER_ID, avatarUri)
        if (current.email.isNotBlank()) {
            userDao.updateUserAvatar("user_${current.email.trim().lowercase()}", avatarUri)
        }
    }

    override suspend fun updateTechAvatar(avatarUri: String?) {
        val current = _currentTech.value
        val updated = current.copy(avatarUri = avatarUri)
        _currentTech.value = updated
        userDao.upsertUser(updated.toEntity())
        userDao.updateUserAvatar(Constants.DEFAULT_TECH_ID, avatarUri)
        if (current.email.isNotBlank()) {
            userDao.updateUserAvatar("tech_${current.email.trim().lowercase()}", avatarUri)
        }
    }

    override suspend fun addCertificate(
        technicianId: String,
        title: String,
        institution: String,
        fileName: String,
        localFilePath: String,
        mimeType: String
    ) {
        val cert = CertificateEntity(
            id = "cert_${System.currentTimeMillis()}",
            technicianId = technicianId,
            title = title.trim(),
            institution = institution.trim(),
            fileName = fileName.trim(),
            localFilePath = localFilePath,
            mimeType = mimeType
        )
        userDao.insertCertificate(cert)
    }

    override suspend fun deleteCertificate(certificateId: String) {
        val existing = userDao.getCertificateById(certificateId)
        if (existing != null && existing.localFilePath.isNotBlank()) {
            try {
                File(existing.localFilePath).takeIf { it.exists() }?.delete()
            } catch (_: Exception) {
            }
        }
        userDao.deleteCertificateById(certificateId)
    }

    override suspend fun logoutUser() {
        // Limpia únicamente la sesión en DataStore; mantiene las tablas de Room intactas
        sessionManager?.clearSession()
    }
}
