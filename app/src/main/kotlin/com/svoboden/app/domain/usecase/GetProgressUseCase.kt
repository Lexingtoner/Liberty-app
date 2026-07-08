package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.JournalRepository
import javax.inject.Inject

data class HabitProgress(
    val habit: Habit,
    val streak: StreakResult,
    val totalCleanDays: Int,
    val moneySaved: Double
)

class GetProgressUseCase @Inject constructor(
    private val habitRepo: HabitRepository,
    private val calculateStreak: CalculateStreakUseCase,
    private val journalRepo: JournalRepository
) {
    suspend operator fun invoke(habitId: Long): HabitProgress {
        val habit = habitRepo.getById(habitId) ?: error("Habit $habitId not found")
        val streak = calculateStreak(habitId)
        val cleanDays = journalRepo.countCleanDays(habitId)
        val moneySaved = cleanDays * habit.dailyCost
        return HabitProgress(habit, streak, cleanDays, moneySaved)
    }
}
