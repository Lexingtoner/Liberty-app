package com.svoboden.app.core.security

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.svoboden.app.data.preferences.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    private val _isLocked = MutableStateFlow(value = false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private var backgroundedAtMillis: Long? = null
    private var lockEnabled = false
    private var timeoutSeconds = 0
    private var attached = false

    /** Вызывается один раз при холодном старте, ДО первого кадра UI. */
    suspend fun restoreInitialState() {
        lockEnabled = appPreferences.biometricLockEnabled.first()
        timeoutSeconds = appPreferences.lockTimeoutSeconds.first()
        _isLocked.value = lockEnabled
    }

    /** Вызывается один раз после Ready — начинает отслеживать сворачивания. */
    fun attachTo(processLifecycleOwner: LifecycleOwner, scope: CoroutineScope) {
        if (attached) return
        attached = true

        scope.launch {
            appPreferences.biometricLockEnabled.collectLatest { lockEnabled = it }
        }
        scope.launch {
            appPreferences.lockTimeoutSeconds.collectLatest { timeoutSeconds = it }
        }

        processLifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    backgroundedAtMillis = System.currentTimeMillis()
                }
                override fun onStart(owner: LifecycleOwner) {
                    if (!lockEnabled) return
                    val backgroundedAt = backgroundedAtMillis ?: return
                    val elapsedSeconds = (System.currentTimeMillis() - backgroundedAt) / 1000
                    if (elapsedSeconds >= timeoutSeconds) _isLocked.value = true
                }
            }
        )
    }

    fun unlock() {
        _isLocked.value = false
        backgroundedAtMillis = null
    }

    fun lockNow() {
        _isLocked.value = true
    }
}
