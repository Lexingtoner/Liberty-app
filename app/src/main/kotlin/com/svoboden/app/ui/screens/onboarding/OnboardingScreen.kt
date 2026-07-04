package com.svoboden.app.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.model.HabitType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    LaunchedEffect(uiState.isCompleted) { if (uiState.isCompleted) onComplete() }
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let(viewModel::setStartDate)
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Каждый день — это новый шанс",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Выберите, от чего хотите освободиться. Без осуждения — только поддержка.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                Text("Ваши привычки", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabitType.entries.forEach { type ->
                        val selected = type in uiState.selectedTypes
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.toggleHabit(type) },
                            label = { Text(type.displayName) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }
            item {
                Text("Дата начала воздержания", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                DatePicker(state = datePickerState, showModeToggle = false)
            }
            item {
                Text("Ваша личная цель", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.motivation,
                    onValueChange = viewModel::setMotivation,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ради чего вы делаете этот шаг?") },
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item {
                Button(
                    onClick = viewModel::completeOnboarding,
                    enabled = uiState.selectedTypes.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Начать свободную жизнь →", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}
