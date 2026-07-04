package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val habitRepo: HabitRepository
) {
    suspend operator fun invoke(
        habitId: Long,
        unit: String,
        goalDays: Int?,
        customName: String?
    ) {
        val current = habitRepo.getById(habitId) ?: error("Habit $habitId not found")
        // ФИКС: isForever теперь вычисляемое свойство (Habit.isForever get() = goalDays == null),
        // поэтому его больше не нужно проставлять вручную и синхронизировать с goalDays.
        val updated = current.copy(unit = unit, goalDays = goalDays, customName = customName)
        habitRepo.saveHabits(listOf(updated))
    }
}
