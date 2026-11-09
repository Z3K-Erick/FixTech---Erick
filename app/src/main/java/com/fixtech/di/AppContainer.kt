package com.fixtech.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.fixtech.FixTechApp
import com.fixtech.data.local.FixTechDatabase
import com.fixtech.data.local.SessionManager
import com.fixtech.data.remote.api.FixTechApiService
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
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    companion object {
        /**
         * URL base del servidor Spring Boot (módulo backend/).
         * - En Emulador de Android Studio: usa "http://10.0.2.2:8080/"
         * - En Celular Físico (misma red Wi-Fi): cámbiala por la IP local de tu PC,
         *   por ejemplo: "http://192.168.1.X:8080/" (puedes ver tu IP con `ipconfig`).
         */
        const val BASE_URL = "http://10.0.2.2:8080/"
    }

    private val appContext = context.applicationContext

    val database: FixTechDatabase by lazy {
        FixTechDatabase.getInstance(appContext)
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(appContext)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .writeTimeout(4, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val apiService: FixTechApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FixTechApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            userDao = database.userDao(),
            sessionManager = sessionManager,
            apiService = apiService
        )
    }

    val deviceRepository: DeviceRepository by lazy {
        DeviceRepositoryImpl(
            deviceDao = database.deviceDao(),
            apiService = apiService
        )
    }

    val guideRepository: GuideRepository by lazy {
        GuideRepositoryImpl(
            guideDao = database.guideDao(),
            apiService = apiService
        )
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
