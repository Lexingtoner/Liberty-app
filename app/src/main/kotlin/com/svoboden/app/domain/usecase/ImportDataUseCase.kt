package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.model.ExportData
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.JournalRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed class ImportResult {
    data class Success(val habitsCount: Int, val entriesCount: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

class ImportDataUseCase @Inject constructor(
    private val habitRepo: HabitRepository,
    private val journalRepo: JournalRepository,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(jsonString: String): ImportResult = try {
        val data = json.decodeFromString(ExportData.serializer(), jsonString)
        if (data.habits.isEmpty()) {
            ImportResult.Error("Файл не содержит привычек")
        } else {
            // Импортируем привычки БЕЗ исходных id (чтобы не столкнуться с чужими
            // id из другого устройства) — saveHabits присвоит новые id активного профиля.
            val savedHabits = habitRepo.saveHabits(data.habits.map { it.copy(id = 0) })
            val oldToNewHabitId = data.habits.zip(savedHabits) { old, new -> old.id to new.id }.toMap()

            data.entries.forEach { entry ->
                val newHabitId = oldToNewHabitId[entry.habitId] ?: return@forEach
                journalRepo.addEntry(entry.copy(id = 0, habitId = newHabitId))
            }
            ImportResult.Success(savedHabits.size, data.entries.size)
        }
    } catch (_: SerializationException) {
        ImportResult.Error("Файл повреждён или имеет неверный формат")
    } catch (e: Exception) {
        ImportResult.Error(e.message ?: "Неизвестная ошибка импорта")
    }
}
