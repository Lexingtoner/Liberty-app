package com.svoboden.app.domain.repository

import com.svoboden.app.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeAll(): Flow<List<Profile>>
    suspend fun getActive(): Profile?
    suspend fun getFirst(): Profile?
    suspend fun create(profile: Profile): Long
    suspend fun setActive(profileId: Long)
    suspend fun delete(profileId: Long)
    suspend fun count(): Int
}
