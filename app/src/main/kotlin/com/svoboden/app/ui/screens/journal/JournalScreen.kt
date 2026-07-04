package com.svoboden.app.ui.screens.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.model.JournalEntry
import com.svoboden.app.domain.model.TriggerTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel = hiltViewModel()) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(habits) {
        if (uiState.selectedHabitId == null) habits.firstOrNull()?.let { viewModel.selectHabit(it.id) }
    }
    LaunchedEffect(Unit) {
        viewModel.newAchievements.collect { achievements ->
            achievements.forEach { type ->
                snackbarHostState.showSnackbar("🏆 Новая награда: ${type.title}", duration = SnackbarDuration.Long)
            }
        }
    }

    if (uiState.showRelapseDialog) {
        RelapseDialog(
            onDismiss = viewModel::hideRelapseDialog,
        ) { triggerId, note ->
            uiState.selectedHabitId?.let { viewModel.logRelapse(it, triggerId, note) }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Дневник") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (habits.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(habits) { habit ->
                        FilterChip(
                            selected = habit.id == uiState.selectedHabitId,
                            onClick = { viewModel.selectHabit(habit.id) },
                            label = { Text(habit.type.displayName) }
                        )
                    }
                }
            }

            if (!uiState.todayLogged) {
                Text("Как прошёл сегодняшний день?", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { uiState.selectedHabitId?.let(viewModel::logCleanDay) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(16.dp)
                    ) { Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text("Чисто ✓") }
                    OutlinedButton(
                        onClick = viewModel::showRelapseDialog,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Был срыв") }
                }
            } else {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.tertiary)
                        Text("Сегодня уже отмечено. Молодец, не сдавайся!")
                    }
                }
            }

            HorizontalDivider()
            Text("История", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.entries, key = { it.id }) { entry -> JournalEntryRow(entry) }
            }
        }
    }
}

@Composable
fun JournalEntryRow(entry: JournalEntry) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(SimpleDateFormat("dd MMM, HH:mm", Locale("ru")).format(Date(entry.date)), style = MaterialTheme.typography.bodyMedium)
        Text(if (entry.hadRelapse) "Срыв" else "Чисто", color = if (entry.hadRelapse) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelapseDialog(onDismiss: () -> Unit, onConfirm: (triggerId: Long?, note: String) -> Unit) {
    val triggers = remember { TriggerTemplate.defaults() }
    var selectedTrigger by remember { mutableStateOf<TriggerTemplate?>(null) }
    var customNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Что произошло?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Срывы — часть пути. Важно понять причину.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    triggers.forEach { trigger ->
                        FilterChip(selected = selectedTrigger?.id == trigger.id, onClick = { selectedTrigger = trigger }, label = { Text(trigger.label) })
                    }
                }
                OutlinedTextField(
                    value = customNote,
                    onValueChange = { customNote = it },
                    placeholder = { Text("Свои слова (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = { Button(onClick = { onConfirm(selectedTrigger?.id, customNote) }) { Text("Записать и начать заново") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
