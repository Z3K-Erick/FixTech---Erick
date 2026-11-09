package com.fixtech.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fixtech.data.local.entity.CertificateEntity
import com.fixtech.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND role = :role LIMIT 1")
    suspend fun getUserByEmailAndRole(email: String, role: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :userId")
    suspend fun updateNotifications(userId: String, enabled: Boolean)

    @Query("SELECT * FROM certificates")
    fun observeAllCertificates(): Flow<List<CertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: CertificateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificates(certificates: List<CertificateEntity>)

    @Query("SELECT COUNT(*) FROM certificates")
    suspend fun getCertificatesCount(): Int
}
