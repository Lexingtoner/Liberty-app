package com.svoboden.app.domain.repository

import com.svoboden.app.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabits(): Flow<List<Habit>>

    /** Возвращает сохранённые привычки с уже проставленными БД-идентификаторами (id > 0). */
    suspend fun saveHabits(habits: List<Habit>): List<Habit>

    suspend fun getById(habitId: Long): Habit?
    suspend fun deleteHabit(habit: Habit)
}
