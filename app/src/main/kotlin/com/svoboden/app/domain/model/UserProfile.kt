package com.svoboden.app.domain.model

enum class ThemeMode { LIGHT, DARK, SYSTEM }

/**
 * Настройки приложения в целом (тема и т.п.), НЕ путать с Profile (глава 9) —
 * Profile — это семейный профиль пользователя со своими привычками,
 * UserProfile — глобальные настройки уровня приложения (одна запись на устройство).
 */
data class UserProfile(
    val id: Long = 1,
    val motivation: String = "",
    val onboardingCompleted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
