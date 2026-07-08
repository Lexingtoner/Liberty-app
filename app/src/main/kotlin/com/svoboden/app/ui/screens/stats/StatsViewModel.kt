package com.svoboden.app.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.usecase.DayStat
import com.svoboden.app.domain.usecase.DayStatus
import com.svoboden.app.domain.usecase.GetBestStreakUseCase
import com.svoboden.app.domain.usecase.GetDailyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val selectedHabitId: Long? = null,
    val isLoading: Boolean = false,
    val dailyStats: List<DayStat> = emptyList(),
    val bestStreakDays: Long = 0,
    val cleanDaysCount: Int = 0,
    val recentEntries: List<com.svoboden.app.domain.model.JournalEntry> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    private val journalRepo: com.svoboden.app.domain.repository.JournalRepository,
    private val getDailyStats: GetDailyStatsUseCase,
    private val getBestStreak: GetBestStreakUseCase
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = habitRepo.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    fun selectHabit(habitId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, selectedHabitId = habitId) }
        val stats = getDailyStats(habitId)
        val bestStreakDays = getBestStreak(habitId)
        
        journalRepo.observeEntries(habitId).collectLatest { entries ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    dailyStats = stats,
                    bestStreakDays = bestStreakDays,
                    cleanDaysCount = stats.count { s -> s.status == DayStatus.CLEAN },
                    recentEntries = entries.sortedByDescending { e -> e.date }
                )
            }
        }
    }

    fun addQuickNote(text: String) = viewModelScope.launch {
        val habitId = _uiState.value.selectedHabitId ?: return@launch
        journalRepo.addEntry(
            com.svoboden.app.domain.model.JournalEntry(
                habitId = habitId,
                date = System.currentTimeMillis(),
                hadRelapse = false,
                customNote = text
            )
        )
    }

    fun logTrigger(triggerId: Long) = viewModelScope.launch {
        val habitId = _uiState.value.selectedHabitId ?: return@launch
        journalRepo.addEntry(
            com.svoboden.app.domain.model.JournalEntry(
                habitId = habitId,
                date = System.currentTimeMillis(),
                hadRelapse = true,
                triggerId = triggerId
            )
        )
    }
}
