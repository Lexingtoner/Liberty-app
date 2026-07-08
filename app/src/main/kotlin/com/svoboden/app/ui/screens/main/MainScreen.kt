package com.svoboden.app.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.svoboden.app.core.navigation.Screen
import com.svoboden.app.ui.screens.community.CommunityScreen
import com.svoboden.app.ui.screens.home.HomeScreen
import com.svoboden.app.ui.screens.onboarding.OnboardingIntroScreen
import com.svoboden.app.ui.screens.onboarding.OnboardingScreen
import com.svoboden.app.ui.screens.onboarding.OnboardingViewModel
import com.svoboden.app.ui.screens.profile.ProfileScreen
import com.svoboden.app.ui.screens.track.TrackScreen

@Composable
fun MainScreen(
    initialOnboardingDone: Boolean,
    onEditHabit: (Long) -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToProfileSelect: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val startDestination = if (initialOnboardingDone) Screen.Home.route else Screen.OnboardingIntro.route

    val onboardingRoutes = listOf(Screen.OnboardingIntro.route, Screen.Onboarding.route)
    val showBottomBar = currentDestination?.route !in onboardingRoutes

    val items = listOf(
        NavigationItem("Главная", Screen.Home, Icons.Default.Home),
        NavigationItem("Сообщество", Screen.Community, Icons.Default.Group),
        NavigationItem("Трек", Screen.Track, Icons.Default.Analytics),
        NavigationItem("Профиль", Screen.Profile, Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.OnboardingIntro.route) {
                OnboardingIntroScreen(
                    onFinished = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.OnboardingIntro.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onEditHabit = onEditHabit,
                    onNavigateToJournal = onNavigateToJournal
                )
            }
            composable(Screen.Community.route) {
                CommunityScreen()
            }
            composable(Screen.Track.route) {
                TrackScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToProfileSelect = onNavigateToProfileSelect,
                    onEditHabit = onEditHabit
                )
            }
        }
    }
}

private data class NavigationItem(
    val title: String,
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
