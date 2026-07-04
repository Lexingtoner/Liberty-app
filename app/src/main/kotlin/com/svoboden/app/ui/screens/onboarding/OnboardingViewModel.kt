package com.svoboden.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.model.HabitType
import com.svoboden.app.domain.model.UserProfile
import com.svoboden.app.domain.repository.UserProfileRepository
import com.svoboden.app.domain.usecase.SaveHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val selectedTypes: Set<HabitType> = emptySet(),
    val startDate: Long = System.currentTimeMillis(),
    val motivation: String = "",
    val isCompleted: Boolean = false,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveHabitsUseCase: SaveHabitsUseCase,
    private val userProfileRepo: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun toggleHabit(type: HabitType) {
        val current = _uiState.value.selectedTypes.toMutableSet()
        if (type in current) current.remove(type) else current.add(type)
        _uiState.update { it.copy(selectedTypes = current) }
    }

    fun setStartDate(millis: Long) = _uiState.update { it.copy(startDate = millis) }
    fun setMotivation(text: String) = _uiState.update { it.copy(motivation = text) }

    fun completeOnboarding() = viewModelScope.launch {
        val state = _uiState.value
        val habits = state.selectedTypes.map { type ->
            Habit(type = type, unit = type.defaultUnit, goalDays = null)
        }
        saveHabitsUseCase(habits, state.startDate)
        userProfileRepo.saveProfile(
            UserProfile(motivation = state.motivation, onboardingCompleted = true)
        )
        _uiState.update { it.copy(isCompleted = true) }
    }
}
