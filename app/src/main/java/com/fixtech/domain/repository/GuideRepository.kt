package com.fixtech.domain.repository

import com.fixtech.domain.model.Guide
import kotlinx.coroutines.flow.Flow

interface GuideRepository {
    fun observeAllGuides(currentUserId: String): Flow<List<Guide>>
    fun observeGuidesForDevice(deviceId: String, currentUserId: String): Flow<List<Guide>>
    fun observeFavoriteGuidesCount(userId: String): Flow<Int>
    fun observeTechGuidesCount(technicianId: String): Flow<Int>
    suspend fun toggleFavoriteGuide(userId: String, deviceId: String, guideId: String)
    suspend fun insertGuideWithSteps(guide: Guide)
    suspend fun deleteGuideCascade(guideId: String)
    suspend fun ensureSeedData()
}
