package com.svoboden.app.domain.repository

import com.svoboden.app.domain.model.StartSession
import kotlinx.coroutines.flow.Flow

/**
 * ФИКС: этот интерфейс упоминался и использовался в CalculateStreakUseCase,
 * LogJournalEntryUseCase, SaveHabitsUseCase и GetBestStreakUseCase на всём
 * протяжении диалога, но домен-интерфейс никогда не был формализован —
 * только SessionDao на уровне data-слоя. Без него use case'ы не скомпилировались бы.
 */
interface SessionRepository {
    suspend fun getActiveSession(habitId: Long): StartSession?
    suspend fun startSession(habitId: Long, startedAt: Long = System.currentTimeMillis())
    suspend fun closeActiveSession(habitId: Long, closedAt: Long)
    fun observeByHabit(habitId: Long): Flow<List<StartSession>>
}
