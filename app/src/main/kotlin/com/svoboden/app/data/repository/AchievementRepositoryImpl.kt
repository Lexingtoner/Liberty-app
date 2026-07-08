package com.svoboden.app.data.repository

import com.svoboden.app.data.local.dao.AchievementDao
import com.svoboden.app.data.local.entity.UnlockedAchievementEntity
import com.svoboden.app.domain.model.UnlockedAchievement
import com.svoboden.app.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
class AchievementRepositoryImpl @Inject constructor(
    private val dao: AchievementDao
) : AchievementRepository {

    override fun observeUnlocked(habitId: Long): Flow<List<UnlockedAchievement>> =
        dao.observeByHabit(habitId).map { list -> list.map { it.toDomain() } }

    override fun observeAllUnlocked(): Flow<List<UnlockedAchievement>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun unlock(habitId: Long, key: String): Boolean {
        if (dao.isUnlocked(habitId, key)) return false
        dao.insert(UnlockedAchievementEntity(habitId = habitId, achievementKey = key, unlockedAt = System.currentTimeMillis()))
        return true
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun UnlockedAchievementEntity.toDomain() = UnlockedAchievement(id, habitId, achievementKey, unlockedAt)
}
