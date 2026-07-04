package com.svoboden.app.domain.repository

import com.svoboden.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun completeOnboarding()
}
