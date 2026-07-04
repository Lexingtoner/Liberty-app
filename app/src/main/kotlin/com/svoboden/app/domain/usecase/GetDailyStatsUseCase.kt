package com.svoboden.app.domain.usecase

import com.svoboden.app.domain.repository.JournalRepository
import javax.inject.Inject

enum class DayStatus { CLEAN, RELAPSE, NO_DATA }
data class DayStat(val dateMillis: Long, val status: DayStatus)

class GetDailyStatsUseCase @Inject constructor(
    private val journalRepo: JournalRepository
) {
    suspend operator fun invoke(habitId: Long, days: Int = 30): List<DayStat> {
        val to = System.currentTimeMillis()
        val from = to - days * 24 * 3_600_000L
        val entries = journalRepo.getEntriesInRange(habitId, from, to)

        return (0 until days).map { offset ->
            val dayStart = startOfDay(to - offset * 24 * 3_600_000L)
            val dayEnd = dayStart + 24 * 3_600_000L
            val dayEntry = entries.firstOrNull { it.date in dayStart until dayEnd }
            DayStat(
                dateMillis = dayStart,
                status = when {
                    dayEntry == null -> DayStatus.NO_DATA
                    dayEntry.hadRelapse -> DayStatus.RELAPSE
                    else -> DayStatus.CLEAN
                }
            )
        }.reversed()
    }

    private fun startOfDay(millis: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
