package com.svoboden.app.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.core.utils.WidgetUpdater
import com.svoboden.app.domain.model.AchievementType
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.model.JournalEntry
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.JournalRepository
import com.svoboden.app.domain.usecase.LogJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val selectedHabitId: Long? = null,
    val entries: List<JournalEntry> = emptyList(),
    val todayLogged: Boolean = false,
    val showRelapseDialog: Boolean = false,
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val logEntryUseCase: LogJournalEntryUseCase,
    private val journalRepo: JournalRepository,
    private val habitRepo: HabitRepository,
    private val widgetUpdater: WidgetUpdater
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = habitRepo.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    private val _newAchievements = MutableSharedFlow<List<AchievementType>>()
    val newAchievements = _newAchievements.asSharedFlow()

    fun selectHabit(habitId: Long) {
        _uiState.update { it.copy(selectedHabitId = habitId, todayLogged = false) }
        viewModelScope.launch {
            journalRepo.observeEntries(habitId).collectLatest { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }

    fun logCleanDay(habitId: Long) = viewModelScope.launch {
        val unlocked = logEntryUseCase(habitId, hadRelapse = false)
        widgetUpdater.updateAll()
        _uiState.update { it.copy(todayLogged = true) }
        if (unlocked.isNotEmpty()) _newAchievements.emit(unlocked)
    }

    fun logRelapse(habitId: Long, triggerId: Long?, note: String) = viewModelScope.launch {
        logEntryUseCase(habitId, hadRelapse = true, triggerId = triggerId, note = note)
        widgetUpdater.updateAll()
        _uiState.update { it.copy(todayLogged = true, showRelapseDialog = false) }
    }

    fun showRelapseDialog() = _uiState.update { it.copy(showRelapseDialog = true) }
    fun hideRelapseDialog() = _uiState.update { it.copy(showRelapseDialog = false) }
}
