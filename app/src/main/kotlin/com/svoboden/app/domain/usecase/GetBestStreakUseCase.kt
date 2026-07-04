package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBestStreakUseCase @Inject constructor(
    private val sessionRepo: SessionRepository,
) {
    /** @return лучшая серия в днях. */
    suspend operator fun invoke(habitId: Long): Long {
        val sessions = sessionRepo.observeByHabit(habitId).first()
        val bestMs = sessions.maxOfOrNull { session ->
            val end = session.restartedAt ?: System.currentTimeMillis()
            end - session.startedAt
        } ?: 0L
        return bestMs / (24 * 3_600_000L)
    }
}
