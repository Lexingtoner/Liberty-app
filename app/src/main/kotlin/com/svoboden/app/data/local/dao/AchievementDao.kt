package com.svoboden.app.data.local.dao

import androidx.room.*
import com.svoboden.app.data.local.entity.UnlockedAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM unlocked_achievements WHERE habitId = :habitId")
    fun observeByHabit(habitId: Long): Flow<List<UnlockedAchievementEntity>>

    @Query("SELECT * FROM unlocked_achievements")
    fun observeAll(): Flow<List<UnlockedAchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: UnlockedAchievementEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_achievements WHERE habitId = :habitId AND achievementKey = :key)")
    suspend fun isUnlocked(habitId: Long, key: String): Boolean
}
