package com.svoboden.app.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.core.security.AppLockManager
import com.svoboden.app.core.security.BiometricAvailability
import com.svoboden.app.data.preferences.AppPreferences
import com.svoboden.app.domain.model.ThemeMode
import com.svoboden.app.domain.model.UserProfile
import com.svoboden.app.domain.repository.ProfileRepository
import com.svoboden.app.domain.repository.UserProfileRepository
import com.svoboden.app.domain.usecase.ExportDataUseCase
import com.svoboden.app.domain.usecase.ImportDataUseCase
import com.svoboden.app.domain.usecase.ImportResult
import com.svoboden.app.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportResult {
    object Success : ExportResult()
    data class Error(val msg: String) : ExportResult()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val userProfileRepo: UserProfileRepository,
    private val profileRepo: ProfileRepository,
    private val reminderScheduler: ReminderScheduler,
    private val appPreferences: AppPreferences,
    private val biometricAvailability: BiometricAvailability,
    private val appLockManager: AppLockManager
) : ViewModel() {

    val profile = userProfileRepo.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val activeFamilyProfile = kotlinx.coroutines.flow.flow {
        emit(profileRepo.getActive())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val reminderEnabled = appPreferences.reminderEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val biometricLockEnabled = appPreferences.biometricLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val dynamicColorEnabled = appPreferences.dynamicColorEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val lockTimeoutSeconds = appPreferences.lockTimeoutSeconds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _exportResult = MutableSharedFlow<ExportResult>()
    val exportResult = _exportResult.asSharedFlow()

    private val _importResult = MutableSharedFlow<ImportResult>()
    val importResult = _importResult.asSharedFlow()

    fun biometricStatus(): BiometricAvailability.Status = biometricAvailability.check()

    fun changeTheme(mode: ThemeMode) = viewModelScope.launch {
        val current = profile.value ?: UserProfile()
        userProfileRepo.saveProfile(current.copy(themeMode = mode))
    }

    fun toggleDynamicColor(enabled: Boolean) = viewModelScope.launch {
        appPreferences.setDynamicColorEnabled(enabled)
    }

    fun toggleReminder(enabled: Boolean, hour: Int, minute: Int) = viewModelScope.launch {
        appPreferences.setReminderEnabled(enabled)
        appPreferences.setReminderTime(hour, minute)
        if (enabled) reminderScheduler.schedule(hour, minute) else reminderScheduler.cancel()
    }

    fun toggleBiometricLock(enabled: Boolean) = viewModelScope.launch {
        appPreferences.setBiometricLockEnabled(enabled)
        if (enabled) appLockManager.lockNow()
    }

    fun setLockTimeout(seconds: Int) = viewModelScope.launch {
        appPreferences.setLockTimeoutSeconds(seconds)
    }

    fun exportData(uri: Uri, context: Context) = viewModelScope.launch {
        try {
            val json = exportDataUseCase()
            context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                ?: throw IllegalStateException("Не удалось открыть файл для записи")
            _exportResult.emit(ExportResult.Success)
        } catch (e: Exception) {
            _exportResult.emit(ExportResult.Error(e.message ?: "Ошибка экспорта"))
        }
    }

    fun importData(uri: Uri, context: Context) = viewModelScope.launch {
        try {
            val json = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()?.use { it.readText() }
                ?: throw IllegalStateException("Не удалось прочитать файл")
            _importResult.emit(importDataUseCase(json))
        } catch (e: Exception) {
            _importResult.emit(ImportResult.Error(e.message ?: "Ошибка чтения файла"))
        }
    }
}
