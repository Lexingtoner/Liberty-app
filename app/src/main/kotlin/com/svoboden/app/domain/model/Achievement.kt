package com.svoboden.app.domain.model

enum class AchievementType(
    val title: String,
    val description: String,
    val thresholdDays: Int
) {
    FIRST_DAY("Первый шаг", "Продержались первый день", 1),
    WEEK_STREAK("Неделя силы", "7 дней подряд без срыва", 7),
    MONTH_STREAK("Месяц свободы", "30 дней подряд без срыва", 30),
    QUARTER_STREAK("Новая привычка", "90 дней — привычка меняется", 90),
    HALF_YEAR_STREAK("Полгода пути", "180 дней устойчивости", 180),
    YEAR_STREAK("Целый год", "365 дней — вы другой человек", 365);

    companion object {
        fun forStreakDays(days: Long): List<AchievementType> =
            entries.filter { days >= it.thresholdDays }
    }
}

enum class MilestoneAchievementType(val title: String, val description: String) {
    FIRST_JOURNAL_ENTRY("Начало пути", "Сделали первую запись в дневнике"),
    HONEST_RELAPSE_LOG("Честность с собой", "Зафиксировали срыв и не сдались"),
    COMEBACK("Возвращение", "Начали заново после срыва"),
    MULTI_HABIT("Несколько фронтов", "Работаете сразу над 2+ привычками")
}

data class UnlockedAchievement(
    val id: Long = 0,
    val habitId: Long,
    val achievementKey: String, // AchievementType.name или MilestoneAchievementType.name
    val unlockedAt: Long
)
