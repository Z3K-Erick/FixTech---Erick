package com.fixtech.presentation.home

import com.fixtech.core.common.Resource
import com.fixtech.domain.model.Certificate
import com.fixtech.domain.model.Device
import com.fixtech.domain.model.User
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.DeviceRepository
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    private val dummyDevices = listOf(
        Device(
            id = "device_01",
            name = "Lenovo Legion 5",
            model = "15ACH6",
            type = "Laptop",
            technicianId = "tech_01",
            technicianName = "Carlos M.",
            imageUrl = "https://example.com/legion.jpg",
            ram = "16 GB",
            storage = "512 GB SSD",
            processor = "AMD Ryzen 7",
            guidesCount = 2
        ),
        Device(
            id = "device_02",
            name = "Corsair Vengeance",
            model = "i7200",
            type = "PC",
            technicianId = "tech_01",
            technicianName = "Carlos M.",
            imageUrl = "https://example.com/corsair.jpg",
            ram = "32 GB",
            storage = "1 TB SSD",
            processor = "Intel Core i7",
            guidesCount = 1
        )
    )

    private val dummySuccessFlow: Flow<List<Device>> = flowOf(
        when (val result: Resource<List<Device>> = Resource.Success(dummyDevices)) {
            is Resource.Success -> result.data
            else -> emptyList()
        }
    )

    private val fakeDeviceRepository = object : DeviceRepository {
        override fun observeExploreDevices(currentUserId: String): Flow<List<Device>> = dummySuccessFlow
        override fun observeMySavedDevices(currentUserId: String): Flow<List<Device>> = flowOf(emptyList())
        override fun observeSavedDevicesCount(currentUserId: String): Flow<Int> = flowOf(0)
        override fun observeTechDevicesCount(technicianId: String): Flow<Int> = flowOf(2)
        override suspend fun existsDuplicateDevice(
            technicianId: String,
            model: String,
            type: String,
            excludeDeviceId: String?
        ): Boolean = false
        override suspend fun toggleBookmark(userId: String, deviceId: String) {}
        override suspend fun insertDevice(device: Device) {}
        override suspend fun deleteDeviceCascade(deviceId: String) {}
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
    fun loadDevices_emitsSuccessState() = runTest(testDispatcher) {
        viewModel = HomeViewModel(
            deviceRepository = fakeDeviceRepository,
            authRepository = fakeAuthRepository
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(dummyDevices, state.catalogDevices)
        assertEquals(2, state.catalogDevices.size)
        assertEquals("Lenovo Legion 5", state.catalogDevices[0].name)
        assertEquals("Corsair Vengeance", state.catalogDevices[1].name)
        assertFalse("isLoading should finish in false", state.isLoading)
    }
}
