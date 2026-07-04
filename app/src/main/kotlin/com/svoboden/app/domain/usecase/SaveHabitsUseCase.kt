package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.SessionRepository
import javax.inject.Inject

class SaveHabitsUseCase @Inject constructor(
    private val habitRepo: HabitRepository,
    private val sessionRepo: SessionRepository,
) {
    suspend operator fun invoke(habits: List<Habit>, startDate: Long) {
        // ФИКС: saveHabits теперь возвращает привычки с реальными БД id —
        // раньше сессия создавалась по habit.id, который для новых привычек
        // был равен 0 у всех сразу (дефолт конструктора), что ломало FK
        // и путало стрики между несколькими одновременно выбранными привычками.
        val savedHabits = habitRepo.saveHabits(habits)
        savedHabits.forEach { habit ->
            if (sessionRepo.getActiveSession(habit.id) == null) {
                sessionRepo.startSession(habit.id, startDate)
            }
        }
    }
}
