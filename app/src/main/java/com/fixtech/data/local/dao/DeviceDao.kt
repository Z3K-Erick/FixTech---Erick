package com.fixtech.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fixtech.data.local.entity.DeviceEntity
import com.fixtech.data.local.entity.DeviceWithBookmarkRow
import com.fixtech.data.local.entity.UserFavoriteDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query(
        """
        SELECT d.*,
               CASE WHEN ufd.deviceId IS NOT NULL THEN 1 ELSE 0 END AS isBookmarked,
               (SELECT COUNT(*) FROM guides g WHERE g.deviceId = d.id) AS guidesCount
        FROM devices d
        LEFT JOIN user_favorite_devices ufd
               ON d.id = ufd.deviceId AND ufd.userId = :currentUserId
        """
    )
    fun observeExploreDevices(currentUserId: String): Flow<List<DeviceWithBookmarkRow>>

    @Query(
        """
        SELECT d.*,
               1 AS isBookmarked,
               (SELECT COUNT(*) FROM guides g WHERE g.deviceId = d.id) AS guidesCount
        FROM devices d
        INNER JOIN user_favorite_devices ufd
                ON d.id = ufd.deviceId
        WHERE ufd.userId = :currentUserId
        ORDER BY ufd.savedAt DESC
        """
    )
    fun observeMySavedDevices(currentUserId: String): Flow<List<DeviceWithBookmarkRow>>

    @Query(
        """
        SELECT COUNT(*)
        FROM user_favorite_devices ufd
        INNER JOIN devices d ON ufd.deviceId = d.id
        WHERE ufd.userId = :currentUserId
        """
    )
    fun observeSavedDevicesCount(currentUserId: String): Flow<Int>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM user_favorite_devices
            WHERE userId = :userId AND deviceId = :deviceId
        )
        """
    )
    suspend fun isDeviceBookmarked(userId: String, deviceId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteDevice(favorite: UserFavoriteDeviceEntity)

    @Query("DELETE FROM user_favorite_devices WHERE userId = :userId AND deviceId = :deviceId")
    suspend fun deleteFavoriteDevice(userId: String, deviceId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Query("SELECT COUNT(*) FROM devices")
    suspend fun getDevicesCount(): Int
}
