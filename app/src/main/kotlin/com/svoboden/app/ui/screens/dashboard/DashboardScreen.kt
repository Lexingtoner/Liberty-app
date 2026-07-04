package com.svoboden.app.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.usecase.HabitProgress
import com.svoboden.app.domain.usecase.StreakResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToJournal: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onEditHabit: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val progressMap by viewModel.progressMap.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моя свобода") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, "Настройки") }
                }
            )
        },
        floatingActionButton = {
            // Material-паттерн: FAB для главного действия экрана — быстрый переход
            // к отметке сегодняшнего дня без похода через нижнюю навигацию.
            ExtendedFloatingActionButton(
                onClick = onNavigateToJournal,
                icon = { Icon(Icons.Default.Check, null) },
                text = { Text("Отметить день") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Прогресс") })
                NavigationBarItem(selected = false, onClick = onNavigateToJournal, icon = { Icon(Icons.Default.EditCalendar, null) }, label = { Text("Журнал") })
                NavigationBarItem(selected = false, onClick = onNavigateToStats, icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Статистика") })
                NavigationBarItem(selected = false, onClick = onNavigateToAchievements, icon = { Icon(Icons.Default.EmojiEvents, null) }, label = { Text("Награды") })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (habits.isEmpty()) {
                item {
                    Text(
                    text = "Пока нет привычек — начните с онбординга.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    HabitProgressCard(
                        habit = habit,
                        progress = progressMap[habit.id],
                        onEditClick = { onEditHabit(habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitProgressCard(habit: Habit, progress: HabitProgress?, onEditClick: () -> Unit) {
    val streak = progress?.streak
    val (days, hours) = remember(streak) {
        if (streak is StreakResult.Active) {
            val totalHours = streak.elapsedMs / 3_600_000L
            (totalHours / 24 to totalHours % 24)
        } else 0L to 0L
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(habit.customName ?: habit.type.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoalBadge(habit)
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Tune, "Настроить цель") }
                }
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = days.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("дн. $hours ч.", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 4.dp))
            }
            Text("без ${habit.unit}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            habit.goalDays?.let { goal ->
                val fraction = (days.toFloat() / goal).coerceIn(0f, 1f)
                LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)))
                Text("${(fraction * 100).toInt()}% пути к цели $goal дней", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun GoalBadge(habit: Habit) {
    val text = if (habit.isForever) "Навсегда" else "${habit.goalDays} дней"
    Surface(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(50)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondary)
    }
}
