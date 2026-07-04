package com.svoboden.app.data.repository

import com.svoboden.app.data.local.dao.JournalEntryDao
import com.svoboden.app.data.local.entity.JournalEntryEntity
import com.svoboden.app.domain.model.JournalEntry
import com.svoboden.app.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val dao: JournalEntryDao
) : JournalRepository {

    override fun observeEntries(habitId: Long): Flow<List<JournalEntry>> =
        dao.observeByHabit(habitId).map { list -> list.map { it.toDomain() } }

    override suspend fun addEntry(entry: JournalEntry) {
        dao.insert(entry.toEntity())
    }

    override suspend fun getEntriesInRange(habitId: Long, from: Long, to: Long): List<JournalEntry> =
        dao.getInRange(habitId, from, to).map { it.toDomain() }

    override suspend fun countCleanDays(habitId: Long): Int = dao.countCleanDays(habitId)

    private fun JournalEntryEntity.toDomain() =
        JournalEntry(id, habitId, date, hadRelapse, triggerId, customNote, mood)

    private fun JournalEntry.toEntity() =
        JournalEntryEntity(id, habitId, date, hadRelapse, triggerId, customNote, mood)
}
