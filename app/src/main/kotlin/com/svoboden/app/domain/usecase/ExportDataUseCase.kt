package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.model.ExportData
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.JournalRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val habitRepo: HabitRepository,
    private val journalRepo: JournalRepository,
) {
    private val json = Json { prettyPrint = true; encodeDefaults = true }

    suspend operator fun invoke(): String {
        val habits = habitRepo.observeHabits().first()
        val entries = habits.flatMap { journalRepo.observeEntries(it.id).first() }
        val export = ExportData(exportedAt = System.currentTimeMillis(), habits = habits, entries = entries)
        return json.encodeToString(ExportData.serializer(), export)
    }
}
