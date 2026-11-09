package com.fixtech.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.fixtech.FixTechApp
import com.fixtech.data.local.FixTechDatabase
import com.fixtech.data.local.SessionManager
import com.fixtech.data.repository.AuthRepositoryImpl
import com.fixtech.data.repository.DeviceRepositoryImpl
import com.fixtech.data.repository.GuideRepositoryImpl
import com.fixtech.domain.repository.AuthRepository
import com.fixtech.domain.repository.DeviceRepository
import com.fixtech.domain.repository.GuideRepository
import com.fixtech.presentation.auth.AuthViewModel
import com.fixtech.presentation.guide.GuideViewModel
import com.fixtech.presentation.home.HomeViewModel
import com.fixtech.presentation.profile.ProfileViewModel

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: FixTechDatabase by lazy {
        FixTechDatabase.getInstance(appContext)
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(appContext)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            userDao = database.userDao(),
            sessionManager = sessionManager
        )
    }

    val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(database.deviceDao())
    }

    val guideRepository: GuideRepository by lazy {
        GuideRepositoryImpl(database.guideDao())
    }

    val viewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    AuthViewModel(authRepository, sessionManager) as T
                }
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(deviceRepository, authRepository) as T
                }
                modelClass.isAssignableFrom(GuideViewModel::class.java) -> {
                    GuideViewModel(guideRepository, authRepository) as T
                }
                modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                    ProfileViewModel(authRepository, deviceRepository, guideRepository, sessionManager) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

object FixTechViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? FixTechApp
            ?: FixTechApp.instance
            ?: throw IllegalStateException("FixTechApp must be registered in AndroidManifest.xml")
        return app.container.viewModelFactory.create(modelClass)
    }
}
