package com.svoboden.app.domain.repository

import com.svoboden.app.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun observeEntries(habitId: Long): Flow<List<JournalEntry>>
    suspend fun addEntry(entry: JournalEntry)
    suspend fun getEntriesInRange(habitId: Long, from: Long, to: Long): List<JournalEntry>
    suspend fun countCleanDays(habitId: Long): Int
}
