package com.svoboden.app.data.local.dao

import androidx.room.*
import com.svoboden.app.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE isActive = 1 LIMIT 1")
    suspend fun getActive(): ProfileEntity?

    @Query("SELECT * FROM profiles ORDER BY createdAt ASC LIMIT 1")
    suspend fun getFirst(): ProfileEntity?

    @Insert
    suspend fun insert(profile: ProfileEntity): Long

    @Query("UPDATE profiles SET isActive = 0")
    suspend fun clearActive()

    @Query("UPDATE profiles SET isActive = 1 WHERE id = :profileId")
    suspend fun setActive(profileId: Long)

    @Query("DELETE FROM profiles WHERE id = :profileId")
    suspend fun delete(profileId: Long)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int
}
