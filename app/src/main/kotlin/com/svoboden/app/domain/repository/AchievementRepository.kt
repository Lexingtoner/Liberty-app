package com.svoboden.app.domain.repository

import com.svoboden.app.domain.model.UnlockedAchievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeUnlocked(habitId: Long): Flow<List<UnlockedAchievement>>
    fun observeAllUnlocked(): Flow<List<UnlockedAchievement>>
    /** @return true, если ачивка разблокирована впервые (а не уже была открыта). */
    suspend fun unlock(habitId: Long, key: String): Boolean
}
