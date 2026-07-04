package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.repository.SessionRepository
import javax.inject.Inject

sealed class StreakResult {
    data class Active(val elapsedMs: Long) : StreakResult()
    object NoSession : StreakResult()
}

class CalculateStreakUseCase @Inject constructor(
    private val sessionRepo: SessionRepository,
) {
    suspend operator fun invoke(habitId: Long): StreakResult {
        val session = sessionRepo.getActiveSession(habitId) ?: return StreakResult.NoSession
        val elapsedMs = System.currentTimeMillis() - session.effectiveStart
        return StreakResult.Active(elapsedMs)
    }
}
