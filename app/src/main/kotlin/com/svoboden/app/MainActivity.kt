package com.svoboden.app

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.svoboden.app.core.init.AppInitializer
import com.svoboden.app.core.init.InitState
import com.svoboden.app.core.navigation.AppNavGraph
import com.svoboden.app.core.navigation.Screen
import com.svoboden.app.core.security.AppLockManager
import com.svoboden.app.data.preferences.AppPreferences
import com.svoboden.app.domain.model.ThemeMode
import com.svoboden.app.domain.repository.ProfileRepository
import com.svoboden.app.domain.repository.UserProfileRepository
import com.svoboden.app.ui.screens.error.DatabaseErrorScreen
import com.svoboden.app.ui.screens.lock.LockScreen
import com.svoboden.app.ui.theme.SvobodenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var appInitializer: AppInitializer
    @Inject lateinit var appLockManager: AppLockManager
    @Inject lateinit var profileRepository: ProfileRepository
    @Inject lateinit var userProfileRepository: UserProfileRepository
    @Inject lateinit var appPreferences: AppPreferences

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isReady by mutableStateOf(value = false)
        splashScreen.setKeepOnScreenCondition { !isReady }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch { appInitializer.run() }

        setContent {
            // Тема на этом уровне — с дефолтами (ThemeMode.SYSTEM, без Material You).
            // Пользовательские настройки темы читаются только внутри ReadyAppContent,
            // ПОСЛЕ того как БД гарантированно открыта — иначе observeProfile() мог бы
            // triggерить обращение к Room раньше, чем завершится миграция шифрования
            // (см. AppInitializer). До этого момента экран всё равно скрыт под сплэшем.
            SvobodenTheme {
                val initState by appInitializer.state.collectAsStateWithLifecycle()

                isReady = (initState is InitState.Ready || initState is InitState.Error)

                when (initState) {
                    is InitState.Error -> DatabaseErrorScreen { /* TODO: удалить БД и перезапустить процесс */ }
                    InitState.Ready -> ReadyAppContent(
                        appLockManager,
                        profileRepository,
                        userProfileRepository,
                        appPreferences
                    )
                    else -> Box(Modifier.fillMaxSize()) // под системным splash не виден
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
private fun ReadyAppContent(
    appLockManager: AppLockManager,
    profileRepository: ProfileRepository,
    userProfileRepository: UserProfileRepository,
    appPreferences: AppPreferences,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { appLockManager.attachTo(ProcessLifecycleOwner.get(), scope) }

    val isLocked by appLockManager.isLocked.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val profile by userProfileRepository.observeProfile().collectAsStateWithLifecycle(initialValue = null)
    val themeMode = profile?.themeMode ?: ThemeMode.SYSTEM
    val dynamicColorEnabled by appPreferences.dynamicColorEnabled.collectAsStateWithLifecycle(initialValue = false)

    var startDestination by remember { mutableStateOf<String?>(null) }
    var onboardingCompleted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val onboardingDone = userProfileRepository.observeProfile().first()?.onboardingCompleted ?: false
        onboardingCompleted = onboardingDone
        val profileCount = profileRepository.count()
        startDestination = when {
            profileCount <= 1 || !onboardingDone -> Screen.Main.route
            else -> Screen.ProfileSelect.route
        }
    }

    val destination = startDestination ?: return

    // Вложенная SvobodenTheme переопределяет дефолтную (заданную в MainActivity.onCreate)
    // для всего дерева ниже — стандартный Compose-паттерн, MaterialTheme можно
    // переприменять на любом уровне композиции.
    SvobodenTheme(themeMode = themeMode, useDynamicColor = dynamicColorEnabled) {
        Box(Modifier.fillMaxSize()) {
            AppNavGraph(
                navController = navController, 
                startDestination = destination,
                onboardingDone = onboardingCompleted
            )
            AnimatedVisibility(visible = isLocked, enter = fadeIn(), exit = fadeOut()) {
                LockScreen(onUnlocked = appLockManager::unlock)
            }
        }
    }
}
