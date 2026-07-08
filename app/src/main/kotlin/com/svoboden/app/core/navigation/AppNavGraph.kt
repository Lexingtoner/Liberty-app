package com.svoboden.app.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.isPopupLayout
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.svoboden.app.ui.screens.habitedit.HabitEditScreen
import com.svoboden.app.ui.screens.journal.JournalScreen
import com.svoboden.app.ui.screens.main.MainScreen
import com.svoboden.app.ui.screens.onboarding.OnboardingIntroScreen
import com.svoboden.app.ui.screens.onboarding.OnboardingScreen
import com.svoboden.app.ui.screens.profiles.ProfileSelectScreen

private const val MOTION_DURATION_MS = 300

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    onboardingDone: Boolean
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
        route = "root",
        popExitTransition = {
            slideOutHorizontally(tween(MOTION_DURATION_MS)) { it / 4 } +
                fadeOut(tween(MOTION_DURATION_MS / 2))
        }
    ) {

        composable(Screen.Main.route) {
            MainScreen(
                initialOnboardingDone = onboardingDone,
                onEditHabit = { habitId -> navController.navigate(Screen.HabitEdit.createRoute(habitId)) },
                onNavigateToJournal = { navController.navigate(Screen.Journal.route) },
                onNavigateToProfileSelect = {
                    navController.navigate(Screen.ProfileSelect.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Journal.route) { JournalScreen() }

        composable(Screen.ProfileSelect.route) {
            ProfileSelectScreen(
                onProfileSelected = {
                    navController.navigate(Screen.Main.route) {
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
