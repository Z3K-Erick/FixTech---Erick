package com.fixtech.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fixtech.core.common.Constants
import com.fixtech.data.local.dao.DeviceDao
import com.fixtech.data.local.dao.GuideDao
import com.fixtech.data.local.dao.UserDao
import com.fixtech.data.local.entity.CertificateEntity
import com.fixtech.data.local.entity.DeviceEntity
import com.fixtech.data.local.entity.GuideEntity
import com.fixtech.data.local.entity.GuideStepEntity
import com.fixtech.data.local.entity.UserEntity
import com.fixtech.data.local.entity.UserFavoriteDeviceEntity
import com.fixtech.data.local.entity.UserFavoriteGuideEntity

@Database(
    entities = [
        DeviceEntity::class,
        GuideEntity::class,
        GuideStepEntity::class,
        UserFavoriteDeviceEntity::class,
        UserFavoriteGuideEntity::class,
        UserEntity::class,
        CertificateEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FixTechDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao
    abstract fun guideDao(): GuideDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: FixTechDatabase? = null

        fun getInstance(context: Context): FixTechDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FixTechDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
