package com.svoboden.app.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.domain.model.AchievementType
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.model.MilestoneAchievementType
import com.svoboden.app.domain.repository.AchievementRepository
import com.svoboden.app.domain.repository.HabitRepository
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

data class BadgeUiModel(
    val key: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean
)

data class AchievementsUiState(
    val selectedHabitId: Long? = null,
    val streakBadges: List<BadgeUiModel> = emptyList(),
    val milestoneBadges: List<BadgeUiModel> = emptyList()
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    private val achievementRepo: AchievementRepository
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = habitRepo.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    fun selectHabit(habitId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(selectedHabitId = habitId) }
        achievementRepo.observeUnlocked(habitId).collectLatest { unlocked ->
            val unlockedKeys = unlocked.map { it.achievementKey }.toSet()
            _uiState.update {
                it.copy(
                    streakBadges = AchievementType.entries.map { type ->
                        BadgeUiModel(type.name, type.title, type.description, type.name in unlockedKeys)
                    },
                    milestoneBadges = MilestoneAchievementType.entries.map { type ->
                        BadgeUiModel(type.name, type.title, type.description, type.name in unlockedKeys)
                    }
                )
            }
        }
    }
}
