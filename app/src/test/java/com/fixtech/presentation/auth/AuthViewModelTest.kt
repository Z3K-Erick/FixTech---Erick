package com.fixtech.presentation.auth

import com.fixtech.core.common.Resource
import com.fixtech.data.remote.dto.ApiMessageResponseDto
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.User
import com.fixtech.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    private val dummyUser = User(
        id = "user_123",
        fullName = "Test User",
        email = "test@example.com",
        role = "USER"
    )

    private val fakeAuthRepository = object : AuthRepository {
        override val currentUser: StateFlow<User> = MutableStateFlow(dummyUser)
        override val currentTech: StateFlow<User> = MutableStateFlow(dummyUser)

        override fun observeCurrentUserId(): Flow<String> = flowOf(dummyUser.id)
        override fun observeAllCertificates(): Flow<Map<String, List<Certificate>>> = flowOf(emptyMap())
        override fun observeCertificatesForTech(technicianId: String): Flow<List<Certificate>> = flowOf(emptyList())
        override fun observeCertificatesCountForTech(technicianId: String): Flow<Int> = flowOf(0)

        override suspend fun login(email: String, password: String, role: String): Resource<User> {
            return Resource.Success(dummyUser)
        }

        override suspend fun register(
            name: String,
            email: String,
            password: String,
            role: String,
            verificationCode: String
        ): Resource<User> {
            return Resource.Success(dummyUser)
        }

        override suspend fun sendVerificationCode(email: String): Resource<ApiMessageResponseDto> {
            return Resource.Success(ApiMessageResponseDto(success = true, message = "Code sent", demoCode = "123456"))
        }

        override suspend fun loginUser(email: String, password: String): User = dummyUser
        override suspend fun registerUser(fullName: String, email: String, password: String): User = dummyUser
        override suspend fun loginTech(email: String, password: String): User = dummyUser
        override suspend fun registerTech(
            fullName: String,
            email: String,
            workshopCode: String,
            password: String
        ): User = dummyUser

        override suspend fun restoreSession(userId: String, name: String, role: String) {}
        override suspend fun getUserById(userId: String): User? = dummyUser
        override suspend fun updateUserNotifications(enabled: Boolean) {}
        override suspend fun updateTechNotifications(enabled: Boolean) {}
        override suspend fun updateUserAvatar(avatarUri: String?) {}
        override suspend fun updateTechAvatar(avatarUri: String?) {}
        override suspend fun addCertificate(
            technicianId: String,
            title: String,
            institution: String,
            fileName: String,
            localFilePath: String,
            mimeType: String
        ) {}
        override suspend fun deleteCertificate(certificateId: String) {}
        override suspend fun logoutUser() {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository = fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginSuccess_updatesUiState() = runTest(testDispatcher) {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")

        val isValid = viewModel.validateUserLogin()
        assertTrue(isValid)

        // Advance dispatcher to complete coroutines launched in viewModelScope
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse("isLoading should be false after login completes", state.isLoading)
        assertNull("errorMessage should be null on login success", state.errorMessage)
    }
}
