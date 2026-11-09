package com.fixtech.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fixtech.data.local.entity.GuideEntity
import com.fixtech.data.local.entity.GuideStepEntity
import com.fixtech.data.local.entity.GuideWithFavoriteRow
import com.fixtech.data.local.entity.UserFavoriteGuideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideDao {

    @Query(
        """
        SELECT g.*,
               CASE WHEN fav.guideId IS NOT NULL THEN 1 ELSE 0 END AS isFavorite,
               fav.pinnedAt AS pinnedAt
        FROM guides g
        LEFT JOIN user_favorite_guides fav
               ON g.id = fav.guideId AND fav.userId = :currentUserId AND fav.deviceId = :deviceId
        WHERE g.deviceId = :deviceId
           OR g.category = (SELECT d.type FROM devices d WHERE d.id = :deviceId)
        ORDER BY CASE WHEN fav.guideId IS NOT NULL THEN 1 ELSE 0 END DESC, fav.pinnedAt DESC, g.id ASC
        """
    )
    fun observeGuidesForDevice(deviceId: String, currentUserId: String): Flow<List<GuideWithFavoriteRow>>

    @Query(
        """
        SELECT g.*,
               CASE WHEN EXISTS(
                   SELECT 1 FROM user_favorite_guides fav
                   WHERE fav.guideId = g.id AND fav.userId = :currentUserId
               ) THEN 1 ELSE 0 END AS isFavorite,
               (
                   SELECT MAX(fav.pinnedAt) FROM user_favorite_guides fav
                   WHERE fav.guideId = g.id AND fav.userId = :currentUserId
               ) AS pinnedAt
        FROM guides g
        ORDER BY CASE WHEN isFavorite = 1 THEN 1 ELSE 0 END DESC, pinnedAt DESC, g.id ASC
        """
    )
    fun observeAllGuides(currentUserId: String): Flow<List<GuideWithFavoriteRow>>

    @Query("SELECT * FROM guide_steps ORDER BY guideId ASC, stepNumber ASC")
    fun observeAllSteps(): Flow<List<GuideStepEntity>>

    @Query("SELECT * FROM guide_steps WHERE guideId = :guideId ORDER BY stepNumber ASC")
    fun observeStepsForGuide(guideId: String): Flow<List<GuideStepEntity>>

    @Query("SELECT COUNT(*) FROM user_favorite_guides ug INNER JOIN guides g ON ug.guideId = g.id WHERE ug.userId = :userId")
    fun observeFavoriteGuidesCount(userId: String): Flow<Int>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM user_favorite_guides
            WHERE userId = :userId AND deviceId = :deviceId AND guideId = :guideId
        )
        """
    )
    suspend fun isGuideFavorite(userId: String, deviceId: String, guideId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteGuide(favorite: UserFavoriteGuideEntity)

    @Query(
        """
        DELETE FROM user_favorite_guides
        WHERE userId = :userId AND deviceId = :deviceId AND guideId = :guideId
        """
    )
    suspend fun deleteFavoriteGuide(userId: String, deviceId: String, guideId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuide(guide: GuideEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuides(guides: List<GuideEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<GuideStepEntity>)

    @Query("SELECT COUNT(*) FROM guides")
    suspend fun getGuidesCount(): Int
}
