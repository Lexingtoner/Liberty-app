package com.svoboden.app.ui.screens.track

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svoboden.app.ui.screens.stats.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showNoteDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showTriggerDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showNoteDialog) {
        AddNoteDialog(
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                viewModel.addQuickNote(note)
                showNoteDialog = false
            }
        )
    }

    if (showTriggerDialog) {
        com.svoboden.app.ui.screens.journal.RelapseDialog(
            onDismiss = { showTriggerDialog = false },
            onConfirm = { triggerId, _ ->
                triggerId?.let { viewModel.logTrigger(it) }
                showTriggerDialog = false
            }
        )
    }

    LaunchedEffect(habits) {
        if (uiState.selectedHabitId == null) habits.firstOrNull()?.let { viewModel.selectHabit(it.id) }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Трекер прогресса",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006A4E)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = "Уведомления")
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (habits.size > 1) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(habits) { habit ->
                        FilterChip(
                            selected = habit.id == uiState.selectedHabitId,
                            onClick = { viewModel.selectHabit(habit.id) },
                            label = { Text(habit.type.displayName) }
                        )
                    }
                }
            }

            // Карточка текущей цели
            MilestoneCard(
                days = uiState.bestStreakDays.toInt(),
                subtitle = "Ваш рекорд чистоты за всё время"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Календарь
            CalendarSection(
                monthName = "История прогресса",
                stats = uiState.dailyStats
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Лог на сегодня
            Text("Лог на сегодня", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LogActionCard(
                    modifier = Modifier.weight(1f).clickable { showNoteDialog = true },
                    title = "Заметка",
                    subtitle = "Как вы себя чувствуете?",
                    icon = Icons.Default.AddCircleOutline,
                    containerColor = Color(0xFFEBF8FF),
                    iconColor = Color(0xFF3182CE)
                )
                LogActionCard(
                    modifier = Modifier.weight(1f).clickable { showTriggerDialog = true },
                    title = "Триггер",
                    subtitle = "Определите стресс",
                    icon = Icons.Default.ElectricBolt,
                    containerColor = Color(0xFFFFF5F5),
                    iconColor = Color(0xFFC53030)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Последняя запись
            val recentEntry = uiState.recentEntries.firstOrNull { it.customNote != null }
            if (recentEntry != null) {
                RecentNoteCard(
                    text = recentEntry.customNote ?: "",
                    time = "Записано " + android.text.format.DateUtils.getRelativeTimeSpanString(recentEntry.date).toString()
                )
            } else {
                Text(
                    "Нет недавних заметок",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // Плавающая кнопка предупреждения как на скриншоте
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(
                onClick = { showTriggerDialog = true },
                containerColor = Color(0xFFE69100),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.WarningAmber, contentDescription = null)
            }
        }
    }
}

@Composable
fun MilestoneCard(days: Int, subtitle: String) {
    val nextMilestone = when {
        days < 7 -> 7
        days < 30 -> 30
        days < 90 -> 90
        days < 365 -> 365
        else -> days + 30
    }
    val progress = (days.toFloat() / nextMilestone).coerceIn(0f, 1f)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF006A4E))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ТЕКУЩАЯ ВЕХА ($nextMilestone ДН.)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB2F5EA),
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = days.toString(),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = " день",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                )
            }
            Text(text = subtitle, fontSize = 14.sp, color = Color(0xFFB2F5EA))
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Color(0xFF38A169),
                trackColor = Color(0xFF2D3748)
            )
        }
    }
}

@Composable
fun CalendarSection(monthName: String, stats: List<com.svoboden.app.domain.usecase.DayStat>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = monthName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    IconButton(onClick = { }) { Icon(Icons.Default.ChevronLeft, null, tint = Color(0xFF64748B)) }
                    IconButton(onClick = { }) { Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF64748B)) }
                }
            }
            
            // Дни недели
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("П", "В", "С", "Ч", "П", "С", "В").forEach { day ->
                    Text(day, fontSize = 12.sp, color = Color(0xFF94A3B8), modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Сетка календаря
            // Для упрощения возьмем последние 28 дней из статистики
            val recentStats = stats.takeLast(28)
            Column {
                recentStats.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        week.forEach { stat ->
                            val calendar = java.util.Calendar.getInstance().apply { timeInMillis = stat.dateMillis }
                            val dayNum = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            val isToday = android.text.format.DateUtils.isToday(stat.dateMillis)
                            
                            CalendarDay(
                                day = dayNum,
                                isSuccess = stat.status == com.svoboden.app.domain.usecase.DayStatus.CLEAN,
                                isSlip = stat.status == com.svoboden.app.domain.usecase.DayStatus.RELAPSE,
                                isToday = isToday
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Легенда
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                Text(" Успех", fontSize = 12.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.width(24.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE69100)))
                Text(" Пропуск", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun CalendarDay(day: Int, isSuccess: Boolean, isSlip: Boolean, isToday: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isToday) Color(0xFF006A4E) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                color = if (isToday) Color.White else if (day > 26 || day < 1) Color(0xFFCBD5E1) else Color(0xFF1E293B),
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
        if (!isToday) {
            if (isSuccess) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF10B981)))
            else if (isSlip) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFE69100)))
        }
    }
}

@Composable
fun LogActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun RecentNoteCard(text: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, null, tint = Color(0xFF006A4E))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "\"$text\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = time, fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить заметку") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Как ваши дела?") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text) }, enabled = text.isNotBlank()) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
