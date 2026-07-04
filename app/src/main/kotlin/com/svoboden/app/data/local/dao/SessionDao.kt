package com.svoboden.app.data.local.dao

import androidx.room.*
import com.svoboden.app.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE habitId = :habitId AND isActive = 1 LIMIT 1")
    suspend fun getActiveSession(habitId: Long): SessionEntity?

    @Insert
    suspend fun insert(session: SessionEntity): Long

    @Query("UPDATE sessions SET isActive = 0, restartedAt = :closedAt WHERE habitId = :habitId AND isActive = 1")
    suspend fun closeActiveSession(habitId: Long, closedAt: Long)

    @Query("SELECT * FROM sessions WHERE habitId = :habitId ORDER BY startedAt DESC")
    fun observeByHabit(habitId: Long): Flow<List<SessionEntity>>
}
