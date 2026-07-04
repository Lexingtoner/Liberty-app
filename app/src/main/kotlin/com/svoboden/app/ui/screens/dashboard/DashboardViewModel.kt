package com.svoboden.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.usecase.GetProgressUseCase
import com.svoboden.app.domain.usecase.HabitProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    private val getProgressUseCase: GetProgressUseCase,
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = habitRepo.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _progressMap = MutableStateFlow<Map<Long, HabitProgress>>(emptyMap())
    val progressMap: StateFlow<Map<Long, HabitProgress>> = _progressMap.asStateFlow()

    init {
        viewModelScope.launch {
            habits.collectLatest { list ->
                val map = list.associateBy(
                    keySelector = { it.id },
                    valueTransform = { getProgressUseCase(it.id) }
                )
                _progressMap.value = map
            }
        }
    }
}
