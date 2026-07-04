package com.svoboden.app.ui.screens.habitedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.model.HabitType
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GoalOption(val label: String) {
    DAYS_7("7 дней"), DAYS_30("30 дней"), DAYS_90("90 дней"),
    FOREVER("Навсегда"), CUSTOM("Свой срок")
}

data class HabitEditUiState(
    val habit: Habit? = null,
    val unitInput: String = "",
    val customNameInput: String = "",
    val goalOption: GoalOption = GoalOption.FOREVER,
    val customGoalInput: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class HabitEditViewModel @Inject constructor(
    private val habitRepo: HabitRepository,
    private val updateHabitUseCase: UpdateHabitUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Long = checkNotNull(savedStateHandle["habitId"])

    private val _uiState = MutableStateFlow(HabitEditUiState())
    val uiState: StateFlow<HabitEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val habit = habitRepo.getById(habitId) ?: return@launch
            _uiState.update {
                it.copy(
                    habit = habit,
                    unitInput = habit.unit,
                    customNameInput = habit.customName.orEmpty(),
                    goalOption = when (habit.goalDays) {
                        null -> GoalOption.FOREVER
                        7 -> GoalOption.DAYS_7
                        30 -> GoalOption.DAYS_30
                        90 -> GoalOption.DAYS_90
                        else -> GoalOption.CUSTOM
                    },
                    customGoalInput = habit.goalDays?.takeIf { d -> d !in setOf(7, 30, 90) }?.toString().orEmpty()
                )
            }
        }
    }

    fun setUnit(value: String) = _uiState.update { it.copy(unitInput = value) }
    fun setCustomName(value: String) = _uiState.update { it.copy(customNameInput = value) }
    fun setGoalOption(option: GoalOption) = _uiState.update { it.copy(goalOption = option) }
    fun setCustomGoal(value: String) = _uiState.update { it.copy(customGoalInput = value.filter(Char::isDigit)) }

    fun save() = viewModelScope.launch {
        val state = _uiState.value
        val goalDays = when (state.goalOption) {
            GoalOption.FOREVER -> null
            GoalOption.DAYS_7 -> 7
            GoalOption.DAYS_30 -> 30
            GoalOption.DAYS_90 -> 90
            GoalOption.CUSTOM -> state.customGoalInput.toIntOrNull()
        }
        updateHabitUseCase(
            habitId = habitId,
            unit = state.unitInput.ifBlank { state.habit?.unit ?: "" },
            goalDays = goalDays,
            customName = state.customNameInput.ifBlank { null }
        )
        _uiState.update { it.copy(isSaved = true) }
    }
}
