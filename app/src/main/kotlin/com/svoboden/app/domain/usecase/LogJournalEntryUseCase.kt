package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.model.AchievementType
import com.svoboden.app.domain.model.JournalEntry
import com.svoboden.app.domain.repository.JournalRepository
import com.svoboden.app.domain.repository.SessionRepository
import javax.inject.Inject

class LogJournalEntryUseCase @Inject constructor(
    private val journalRepo: JournalRepository,
    private val sessionRepo: SessionRepository,
    private val checkAchievements: CheckAchievementsUseCase,
    private val calculateStreak: CalculateStreakUseCase,
) {
    suspend operator fun invoke(
        habitId: Long,
        hadRelapse: Boolean,
        triggerId: Long? = null,
        note: String? = null
    ): List<AchievementType> {
        val entry = JournalEntry(
            habitId = habitId,
            date = System.currentTimeMillis(),
            hadRelapse = hadRelapse,
            triggerId = triggerId,
            customNote = note
        )
        journalRepo.addEntry(entry)

        if (hadRelapse) {
            sessionRepo.closeActiveSession(habitId, System.currentTimeMillis())
            sessionRepo.startSession(habitId)
            checkAchievements.markHonestRelapseLog(habitId)
            checkAchievements.markComeback(habitId)
            return emptyList()
        }

        val streak = calculateStreak(habitId)
        val streakDays = (streak as? StreakResult.Active)?.elapsedMs?.div(24 * 3_600_000L) ?: 0L
        return checkAchievements(habitId, streakDays)
    }
}
