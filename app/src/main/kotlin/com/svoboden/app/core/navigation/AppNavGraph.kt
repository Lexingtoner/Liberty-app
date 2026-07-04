package com.svoboden.app.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.svoboden.app.ui.screens.achievements.AchievementsScreen
import com.svoboden.app.ui.screens.dashboard.DashboardScreen
import com.svoboden.app.ui.screens.habitedit.HabitEditScreen
import com.svoboden.app.ui.screens.journal.JournalScreen
import com.svoboden.app.ui.screens.onboarding.OnboardingScreen
import com.svoboden.app.ui.screens.profiles.ProfileSelectScreen
import com.svoboden.app.ui.screens.settings.SettingsScreen
import com.svoboden.app.ui.screens.stats.StatsScreen

// Длительность и кривая примерно соответствуют Material 3 motion-токенам
// (--dur-base ~300ms, стандартный easing). Общий для всех переходов — держит
// навигацию предсказуемой, а не самопальной для каждого экрана отдельно.
private const val MOTION_DURATION_MS = 300

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(tween(MOTION_DURATION_MS)) { it / 4 } +
                fadeIn(tween(MOTION_DURATION_MS))
        },
        exitTransition = { fadeOut(tween(MOTION_DURATION_MS / 2)) },
        popEnterTransition = { fadeIn(tween(MOTION_DURATION_MS)) },
        popExitTransition = {
            slideOutHorizontally(tween(MOTION_DURATION_MS)) { it / 4 } +
                fadeOut(tween(MOTION_DURATION_MS / 2))
        }
    ) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToJournal = { navController.navigate(Screen.Journal.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onEditHabit = { habitId -> navController.navigate(Screen.HabitEdit.createRoute(habitId)) }
            )
        }

        composable(Screen.Journal.route) { JournalScreen() }
        composable(Screen.Stats.route) { StatsScreen() }
        composable(Screen.Achievements.route) { AchievementsScreen() }

        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen(
                onNavigateToProfiles = { navController.navigate(Screen.ProfileSelect.route) }
            )
        }

        composable(Screen.ProfileSelect.route) {
            ProfileSelectScreen(
                onProfileSelected = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.HabitEdit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) {
            HabitEditScreen(onSaved = { navController.popBackStack() })
        }
    }
}
