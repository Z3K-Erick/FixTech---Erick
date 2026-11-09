package com.fixtech.data.remote.api

import com.fixtech.data.remote.dto.ApiMessageResponseDto
import com.fixtech.data.remote.dto.AuthResponseDto
import com.fixtech.data.remote.dto.DeviceDto
import com.fixtech.data.remote.dto.GuideDto
import com.fixtech.data.remote.dto.LoginRequestDto
import com.fixtech.data.remote.dto.RegisterRequestDto
import com.fixtech.data.remote.dto.SendCodeRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FixTechApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("api/auth/send-code")
    suspend fun sendVerificationCode(@Body request: SendCodeRequestDto): ApiMessageResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @GET("api/devices")
    suspend fun getDevices(): List<DeviceDto>

    @POST("api/devices")
    suspend fun createDevice(@Body device: DeviceDto): DeviceDto

    @GET("api/guides")
    suspend fun getGuides(): List<GuideDto>

    @POST("api/guides")
    suspend fun createGuide(@Body guide: GuideDto): GuideDto
}
