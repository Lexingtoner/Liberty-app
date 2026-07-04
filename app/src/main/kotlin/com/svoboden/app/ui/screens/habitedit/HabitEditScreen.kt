package com.svoboden.app.ui.screens.habitedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.model.HabitType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEditScreen(onSaved: () -> Unit, viewModel: HabitEditViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onSaved() }
    val habit = uiState.habit ?: return

    Scaffold(
        topBar = { TopAppBar(title = { Text("Настроить: ${habit.type.displayName}") }) },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (habit.type == HabitType.OTHER) {
                Column {
                    Text("Название привычки", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.customNameInput,
                        onValueChange = viewModel::setCustomName,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Например: соцсети") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            Column {
                Text("Единица измерения прогресса", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.unitInput,
                    onValueChange = viewModel::setUnit,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("например: сигарет, дней без алкоголя") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Column {
                Text("Ваша цель", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                FlowRowGoals(uiState.goalOption, viewModel::setGoalOption)
                AnimatedVisibility(visible = uiState.goalOption == GoalOption.CUSTOM) {
                    OutlinedTextField(value = uiState.customGoalInput, onValueChange = viewModel::setCustomGoal,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("Количество дней") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))
                }
            }
            Text("Цель можно менять в любое время — стрик не сбросится.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text("Сохранить") }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowGoals(selected: GoalOption, onSelect: (GoalOption) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GoalOption.entries.forEach { option ->
            FilterChip(selected = selected == option, onClick = { onSelect(option) }, label = { Text(option.label) })
        }
    }
}
