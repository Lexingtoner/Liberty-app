package com.svoboden.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.usecase.GetProgressUseCase
import com.svoboden.app.domain.usecase.HabitProgress
import com.svoboden.app.domain.usecase.LogJournalEntryUseCase
import com.svoboden.app.domain.provider.MotivationProvider
import com.svoboden.app.domain.provider.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardEvent {
    data class ShowMessage(val message: String) : DashboardEvent()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    private val getProgressUseCase: GetProgressUseCase,
    private val logJournalEntry: LogJournalEntryUseCase,
    private val motivationProvider: MotivationProvider
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = habitRepo.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _progressMap = MutableStateFlow<Map<Long, HabitProgress>>(emptyMap())
    val progressMap: StateFlow<Map<Long, HabitProgress>> = _progressMap.asStateFlow()

    private val _totalMoneySaved = MutableStateFlow(0.0)
    val totalMoneySaved: StateFlow<Double> = _totalMoneySaved.asStateFlow()

    private val _healthProgress = MutableStateFlow(0)
    val healthProgress: StateFlow<Int> = _healthProgress.asStateFlow()

    private val _events = kotlinx.coroutines.flow.MutableSharedFlow<DashboardEvent>()
    val events = _events.asSharedFlow()

    val dailyQuote: Quote = motivationProvider.getDailyQuote()

    init {
        viewModelScope.launch {
            habits.collectLatest { list ->
                val map = list.associateBy(
                    keySelector = { it.id },
                    valueTransform = { getProgressUseCase(it.id) }
                )
                _progressMap.value = map
                
                // Расчет общих показателей
                _totalMoneySaved.value = map.values.sumOf { it.moneySaved }
                
                // Здоровье - средний процент выполнения целей или просто макс стрик
                val maxGoal = list.maxOfOrNull { it.goalDays ?: 30 } ?: 30
                val maxStreak = map.values.maxOfOrNull { (it.streak as? com.svoboden.app.domain.usecase.StreakResult.Active)?.elapsedMs ?: 0L } ?: 0L
                val maxStreakDays = (maxStreak / 86_400_000L).toInt()
                _healthProgress.value = ((maxStreakDays.toFloat() / maxGoal) * 100).toInt().coerceIn(0, 100)
            }
        }
    }

    fun logCraving() = viewModelScope.launch {
        val firstHabit = habits.value.firstOrNull() ?: return@launch
        logJournalEntry(
            habitId = firstHabit.id,
            hadRelapse = true,
            note = "Зафиксирована сильная тяга"
        )
        _events.emit(DashboardEvent.ShowMessage("Тяга зафиксирована. Вы справитесь!"))
    }

    fun onNotificationClick() = viewModelScope.launch {
        _events.emit(DashboardEvent.ShowMessage("У вас нет новых уведомлений"))
    }
}
