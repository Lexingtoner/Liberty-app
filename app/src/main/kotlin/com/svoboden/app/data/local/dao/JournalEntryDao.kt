package com.svoboden.app.data.local.dao

import androidx.room.*
import com.svoboden.app.data.local.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries WHERE habitId = :habitId ORDER BY date DESC")
    fun observeByHabit(habitId: Long): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE habitId = :habitId AND date >= :from AND date <= :to")
    suspend fun getInRange(habitId: Long, from: Long, to: Long): List<JournalEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity): Long

    @Query("SELECT COUNT(*) FROM journal_entries WHERE habitId = :habitId AND hadRelapse = 0")
    suspend fun countCleanDays(habitId: Long): Int
}
