package com.svoboden.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "svoboden_prefs")

/**
 * Единый DataStore-класс для всех некритичных (не-БД) настроек приложения.
 *
 * ФИКС: в черновом плане reminderTime описывался как
 * `Flow<Pair<Int, Int>>` напрямую из DataStore — Preferences DataStore
 * не умеет хранить Pair, только примитивы/String/Set<String>. Час и минута
 * хранятся раздельными ключами и объединяются через combine().
 */
@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")

        val BIOMETRIC_LOCK_ENABLED = booleanPreferencesKey("biometric_lock_enabled")
        val LOCK_TIMEOUT_SECONDS = intPreferencesKey("lock_timeout_seconds")

        val DB_ENCRYPTION_DONE = booleanPreferencesKey("db_encryption_done")

        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
    }

    // ── Напоминания ──────────────────────────────────────────
    val reminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.REMINDER_ENABLED] ?: false }

    /** Время напоминания как пара (час, минута), по умолчанию 20:00. */
    val reminderTime: Flow<Pair<Int, Int>> = combine(
        context.dataStore.data.map { it[Keys.REMINDER_HOUR] ?: 20 },
        context.dataStore.data.map { it[Keys.REMINDER_MINUTE] ?: 0 }
    ) { hour, minute -> hour to minute }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REMINDER_ENABLED] = enabled }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.REMINDER_HOUR] = hour
            it[Keys.REMINDER_MINUTE] = minute
        }
    }

    // ── Биометрическая блокировка ───────────────────────────
    val biometricLockEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.BIOMETRIC_LOCK_ENABLED] ?: false }

    /** 0 = блокировать сразу при возврате из фона. */
    val lockTimeoutSeconds: Flow<Int> = context.dataStore.data
        .map { it[Keys.LOCK_TIMEOUT_SECONDS] ?: 0 }

    suspend fun setBiometricLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BIOMETRIC_LOCK_ENABLED] = enabled }
    }

    suspend fun setLockTimeoutSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.LOCK_TIMEOUT_SECONDS] = seconds }
    }

    // ── Шифрование БД ─────────────────────────────────────────
    val dbEncryptionDone: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DB_ENCRYPTION_DONE] ?: false }

    suspend fun setDbEncryptionDone(done: Boolean) {
        context.dataStore.edit { it[Keys.DB_ENCRYPTION_DONE] = done }
    }

    // ── Material You (динамические цвета из обоев, Android 12+) ─
    val dynamicColorEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DYNAMIC_COLOR_ENABLED] ?: false }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR_ENABLED] = enabled }
    }
}
