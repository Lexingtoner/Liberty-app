package com.svoboden.app.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.usecase.DayStat
import com.svoboden.app.domain.usecase.DayStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habits) {
        if (uiState.selectedHabitId == null) habits.firstOrNull()?.let { viewModel.selectHabit(it.id) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Статистика") }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (habits.size > 1) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(habits) { habit ->
                            FilterChip(selected = habit.id == uiState.selectedHabitId, onClick = { viewModel.selectHabit(habit.id) }, label = { Text(habit.type.displayName) })
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatSummaryCard(Modifier.weight(1f), uiState.bestStreakDays.toString(), "Лучшая серия, дн.", Icons.Default.EmojiEvents)
                    StatSummaryCard(Modifier.weight(1f), uiState.cleanDaysCount.toString(), "Чистых дней (30)", Icons.Default.CheckCircle)
                }
            }
            item {
                Text("По дням", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(uiState.dailyStats.reversed()) { day -> DayStatRow(day) }
        }
    }
}

@Composable
fun StatSummaryCard(modifier: Modifier = Modifier, value: String, label: String, icon: ImageVector) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DayStatRow(day: DayStat) {
    val (color, label) = when (day.status) {
        DayStatus.CLEAN -> MaterialTheme.colorScheme.tertiary to "Чисто"
        DayStatus.RELAPSE -> MaterialTheme.colorScheme.error to "Срыв"
        DayStatus.NO_DATA -> MaterialTheme.colorScheme.outline to "Нет записи"
    }
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(SimpleDateFormat("dd MMM", Locale("ru")).format(Date(day.dateMillis)), style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(color))
            Text(label, style = MaterialTheme.typography.bodySmall, color = color)
        }
    }
}
