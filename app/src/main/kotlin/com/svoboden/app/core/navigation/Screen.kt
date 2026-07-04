package com.svoboden.app.core.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Journal : Screen("journal")
    object Stats : Screen("stats")
    object Settings : Screen("settings")
    object Achievements : Screen("achievements")
    object ProfileSelect : Screen("profile_select")

    object HabitEdit : Screen("habit_edit/{habitId}") {
        fun createRoute(habitId: Long) = "habit_edit/$habitId"
    }
}
