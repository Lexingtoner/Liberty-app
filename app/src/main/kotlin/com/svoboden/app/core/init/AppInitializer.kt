package com.svoboden.app.core.init

import com.svoboden.app.core.security.AppLockManager
import com.svoboden.app.core.security.DatabaseEncryptionMigrator
import com.svoboden.app.core.security.DatabaseMigrationException
import com.svoboden.app.core.session.ActiveProfileHolder
import com.svoboden.app.data.local.dao.ProfileDao
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class InitState {
    object NotStarted : InitState()
    object MigratingEncryption : InitState()
    object OpeningDatabase : InitState()
    object LoadingProfile : InitState()
    object RestoringSecurity : InitState()
    object Ready : InitState()
    data class Error(val step: String, val cause: Throwable) : InitState()
}

/**
 * Единая точка входа для порядка инициализации, ранее разбросанного по
 * главам 7-9: шифрование → открытие БД → активный профиль → блокировка.
 * Порядок теперь часть типа (последовательность InitState), а не соглашение
 * между тремя разными @Inject-полями в MainActivity.
 */
@Singleton
class AppInitializer @Inject constructor(
    private val dbMigrator: DatabaseEncryptionMigrator,
    private val profileDao: Lazy<ProfileDao>, // Lazy — чтобы не триггерить открытие БД раньше времени
    private val activeProfileHolder: ActiveProfileHolder,
    private val appLockManager: AppLockManager,
) {
    private val _state = MutableStateFlow<InitState>(InitState.NotStarted)
    val state: StateFlow<InitState> = _state.asStateFlow()

    private var started = false

    /** Идемпотентен: повторный вызов (например, при повороте экрана) не запускает шаги заново. */
    suspend fun run() {
        if (started) return
        started = true

        try {
            _state.value = InitState.MigratingEncryption
            dbMigrator.migrateIfNeeded()

            _state.value = InitState.OpeningDatabase
            withContext(Dispatchers.IO) {
                profileDao.get().getFirst() // лёгкий запрос, триггерящий открытие AppDatabase
            }

            _state.value = InitState.LoadingProfile
            activeProfileHolder.init()

            _state.value = InitState.RestoringSecurity
            appLockManager.restoreInitialState()

            _state.value = InitState.Ready
        } catch (e: DatabaseMigrationException) {
            _state.value = InitState.Error("encryption_migration", e)
        } catch (e: Exception) {
            _state.value = InitState.Error("database_open", e)
        }
    }
}
