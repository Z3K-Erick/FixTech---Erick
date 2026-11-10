package com.fixtech.presentation.guide

import com.fixtech.core.common.Resource
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.Guide
import com.fixtech.domain.model.User
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.GuideRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GuideViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GuideViewModel

    private val dummyNetworkErrorResult: Resource.Error = Resource.Error(
        message = "Error de conexión",
        throwable = Exception("Error de conexión")
    )

    private val fakeGuideRepository = object : GuideRepository {
        override fun observeAllGuides(currentUserId: String): Flow<List<Guide>> = flow {
            throw dummyNetworkErrorResult.throwable ?: Exception(dummyNetworkErrorResult.message)
        }

        override fun observeGuidesForDevice(deviceId: String, currentUserId: String): Flow<List<Guide>> = flow {
            throw dummyNetworkErrorResult.throwable ?: Exception(dummyNetworkErrorResult.message)
        }

        override fun observeFavoriteGuidesCount(userId: String): Flow<Int> = flowOf(0)
        override fun observeTechGuidesCount(technicianId: String): Flow<Int> = flowOf(0)
        override suspend fun toggleFavoriteGuide(userId: String, deviceId: String, guideId: String) {}
        override suspend fun insertGuideWithSteps(guide: Guide) {}
        override suspend fun deleteGuideCascade(guideId: String) {}
        override suspend fun ensureSeedData() {}
        override suspend fun refreshFromRemote() {}
    }

    private val dummyUser = User(
        id = "user_test_01",
        fullName = "Erick Test",
        email = "erick@example.com",
        role = "USER"
    )

    private val fakeAuthRepository = object : AuthRepository {
        override val currentUser: StateFlow<User> = MutableStateFlow(dummyUser)
        override val currentTech: StateFlow<User> = MutableStateFlow(dummyUser)
        override fun observeCurrentUserId(): Flow<String> = flowOf(dummyUser.id)
        override fun observeAllCertificates(): Flow<Map<String, List<Certificate>>> = flowOf(emptyMap())
        override fun observeCertificatesForTech(technicianId: String): Flow<List<Certificate>> = flowOf(emptyList())
        override fun observeCertificatesCountForTech(technicianId: String): Flow<Int> = flowOf(0)
        override suspend fun login(email: String, password: String, role: String): Resource<User> = Resource.Success(dummyUser)
        override suspend fun register(
            name: String,
            email: String,
            password: String,
            role: String,
            verificationCode: String
        ): Resource<User> = Resource.Success(dummyUser)
        override suspend fun sendVerificationCode(email: String) = Resource.Success(
            com.fixtech.data.remote.dto.ApiMessageResponseDto(success = true, message = "OK")
        )
        override suspend fun loginUser(email: String, password: String): User = dummyUser
        override suspend fun registerUser(fullName: String, email: String, password: String): User = dummyUser
        override suspend fun loginTech(email: String, password: String): User = dummyUser
        override suspend fun registerTech(fullName: String, email: String, workshopCode: String, password: String): User = dummyUser
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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadGuides_networkError_updatesErrorUiState() = runTest(testDispatcher) {
        viewModel = GuideViewModel(
            guideRepository = fakeGuideRepository,
            authRepository = fakeAuthRepository
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull("errorMessage should not be null on network error", state.errorMessage)
        assertEquals("Error de conexión", state.errorMessage)
        assertFalse("isLoading should be false when error is caught", state.isLoading)
    }
}
