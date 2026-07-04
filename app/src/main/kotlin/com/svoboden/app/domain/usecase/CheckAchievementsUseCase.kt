package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.model.AchievementType
import com.svoboden.app.domain.model.MilestoneAchievementType
import com.svoboden.app.domain.repository.AchievementRepository
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.JournalRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckAchievementsUseCase @Inject constructor(
    private val achievementRepo: AchievementRepository,
    private val habitRepo: HabitRepository,
    private val journalRepo: JournalRepository
) {
    suspend operator fun invoke(habitId: Long, streakDays: Long): List<AchievementType> {
        val newlyUnlocked = mutableListOf<AchievementType>()

        AchievementType.forStreakDays(streakDays).forEach { type ->
            if (achievementRepo.unlock(habitId, type.name)) newlyUnlocked += type
        }

        checkMilestone(habitId, MilestoneAchievementType.FIRST_JOURNAL_ENTRY) {
            journalRepo.observeEntries(habitId).first().isNotEmpty()
        }
        checkMilestone(habitId, MilestoneAchievementType.MULTI_HABIT) {
            habitRepo.observeHabits().first().size >= 2
        }

        return newlyUnlocked
    }

    suspend fun markHonestRelapseLog(habitId: Long) {
        achievementRepo.unlock(habitId, MilestoneAchievementType.HONEST_RELAPSE_LOG.name)
    }

    suspend fun markComeback(habitId: Long) {
        achievementRepo.unlock(habitId, MilestoneAchievementType.COMEBACK.name)
    }

    private suspend fun checkMilestone(
        habitId: Long,
        type: MilestoneAchievementType,
        condition: suspend () -> Boolean
    ) {
        if (condition()) achievementRepo.unlock(habitId, type.name)
    }
}
