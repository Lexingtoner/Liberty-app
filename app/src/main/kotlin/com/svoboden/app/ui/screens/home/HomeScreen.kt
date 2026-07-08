package com.svoboden.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.usecase.StreakResult
import com.svoboden.app.ui.screens.dashboard.DashboardViewModel

@Composable
fun HomeScreen(
    onEditHabit: (Long) -> Unit,
    onNavigateToJournal: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val progressMap by viewModel.progressMap.collectAsStateWithLifecycle()
    val totalMoney by viewModel.totalMoneySaved.collectAsStateWithLifecycle()
    val healthProgress by viewModel.healthProgress.collectAsStateWithLifecycle()
    val quote = viewModel.dailyQuote
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is com.svoboden.app.ui.screens.dashboard.DashboardEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    // В реальном приложении это будет приходить из репозитория профиля
    val userName = "Alex" 
    
    Scaffold(
        topBar = {
            HomeHeader(userName = userName, onNotificationClick = { viewModel.onNotificationClick() })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Основной прогресс (берем первую привычку или суммарный)
            val firstHabit = habits.firstOrNull()
            val progress = firstHabit?.let { progressMap[it.id] }
            val streak = progress?.streak
            val days = if (streak is StreakResult.Active) streak.elapsedMs / 86_400_000L else 0L
            
            val goal = firstHabit?.goalDays ?: 30
            val progressFraction = (days.toFloat() / goal).coerceIn(0f, 1f)

            MainProgressCircle(
                days = days.toInt(),
                progressFraction = progressFraction,
                habitName = firstHabit?.customName ?: firstHabit?.type?.displayName ?: "Свободный путь"
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Карточки статистики
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    title = "Сэкономлено",
                    value = "$%.0f".format(totalMoney),
                    icon = Icons.Default.Payments,
                    iconColor = Color(0xFFE69100)
                )
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    title = "Здоровье",
                    value = "$healthProgress%",
                    icon = Icons.Default.HealthAndSafety,
                    iconColor = Color(0xFF3B82F6),
                    showTrend = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Карточка мотивации
            MotivationCard(
                quote = quote.text,
                author = quote.author
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка действия
            Button(
                onClick = { viewModel.logCraving() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE69100))
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Записать тягу", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Text(
                text = "Нажмите, когда почувствуете позыв. Мы здесь, чтобы помочь вам продышаться.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HomeHeader(userName: String, onNotificationClick: () -> Unit) {
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
                Text(userName.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Доброе утро, $userName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006A4E)
            )
        }
        IconButton(onClick = onNotificationClick) {
            Icon(Icons.Default.NotificationsNone, contentDescription = "Уведомления")
        }
    }
}

@Composable
fun MainProgressCircle(days: Int, progressFraction: Float, habitName: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        CircularProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF006A4E),
            strokeWidth = 12.dp,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = days.toString(),
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF006A4E)
            )
            Text(
                text = "ДНЕЙ ЧИСТОТЫ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Surface(
        color = Color(0xFFE6FFFA),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = habitName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF006A4E)
        )
    }
}

@Composable
fun StatSmallCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    showTrend: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 12.sp, color = Color(0xFF64748B))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                if (showTrend) {
                    Icon(
                        imageVector = Icons.Default.Bolt, // Заглушка для иконки тренда
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp).padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MotivationCard(quote: String, author: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF004D39))
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            // Большие кавычки на фоне
            Text(
                text = "\"",
                fontSize = 120.sp,
                color = Color(0xFFFFFFFF).copy(alpha = 0.1f),
                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 20.dp, y = 40.dp)
            )
            
            Column {
                Text(
                    text = "ЕЖЕДНЕВНАЯ МОТИВАЦИЯ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB2F5EA),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"$quote\"",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "— $author",
                    fontSize = 14.sp,
                    color = Color(0xFFB2F5EA)
                )
            }
        }
    }
}
