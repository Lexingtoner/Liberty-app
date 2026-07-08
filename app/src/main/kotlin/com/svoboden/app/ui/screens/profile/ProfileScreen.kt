package com.svoboden.app.ui.screens.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.core.security.BiometricAvailability
import com.svoboden.app.domain.usecase.ImportResult
import com.svoboden.app.ui.screens.achievements.AchievementsViewModel
import com.svoboden.app.ui.screens.dashboard.DashboardViewModel
import com.svoboden.app.ui.screens.settings.ExportResult
import com.svoboden.app.ui.screens.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToProfileSelect: () -> Unit,
    onEditHabit: (Long) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    achievementsViewModel: AchievementsViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val achievementsState by achievementsViewModel.uiState.collectAsStateWithLifecycle()
    val habits by achievementsViewModel.habits.collectAsStateWithLifecycle()
    val dashboardProgress by dashboardViewModel.progressMap.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showImportWarning by remember { mutableStateOf(false) }
    var showUnavailableDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { settingsViewModel.exportData(it, context) }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { settingsViewModel.importData(it, context) }
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        settingsViewModel.exportResult.collect { result ->
            val msg = when (result) {
                is ExportResult.Success -> "Данные экспортированы"
                is ExportResult.Error -> result.msg
            }
            snackbarHostState.showSnackbar(msg)
        }
    }
    LaunchedEffect(Unit) {
        settingsViewModel.importResult.collect { result ->
            val msg = when (result) {
                is ImportResult.Success -> "Импортировано: ${result.habitsCount} привычек"
                is ImportResult.Error -> result.message
            }
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(habits) {
        if (achievementsState.selectedHabitId == null) {
            habits.firstOrNull()?.let { achievementsViewModel.selectHabit(it.id) }
        }
    }

    if (showImportWarning) {
        AlertDialog(
            onDismissRequest = { showImportWarning = false },
            title = { Text("Импортировать данные?") },
            text = { Text("Импорт добавит привычки и записи из файла к текущему профилю.") },
            confirmButton = {
                TextButton(onClick = { showImportWarning = false; importLauncher.launch(arrayOf("application/json")) }) { Text("Продолжить") }
            },
            dismissButton = { TextButton(onClick = { showImportWarning = false }) { Text("Отмена") } }
        )
    }

    if (showUnavailableDialog) {
        AlertDialog(
            onDismissRequest = { showUnavailableDialog = false },
            title = { Text("Блокировка недоступна") },
            text = { Text("Сначала настройте блокировку экрана в системе.") },
            confirmButton = { TextButton(onClick = { showUnavailableDialog = false }) { Text("Понятно") } }
        )
    }

    if (showPrivacyDialog) {
        PrivacySettingsDialog(
            onDismiss = { showPrivacyDialog = false },
            viewModel = settingsViewModel,
            onShowUnavailable = { showUnavailableDialog = true },
            onExportClick = { exportLauncher.launch("svoboden_backup.json") },
            onImportClick = { showImportWarning = true }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(1f)
                    .absolutePadding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
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
                        text = "Ваш путь",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            item {
                UserInfoCard(
                    name = "Alex Morgan", 
                    level = achievementsState.level, 
                    points = achievementsState.totalPoints
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                val currentHabitId = achievementsState.selectedHabitId
                val progress = currentHabitId?.let { dashboardProgress[it] }
                val streak = (progress?.streak as? com.svoboden.app.domain.usecase.StreakResult.Active)?.elapsedMs ?: 0L
                val days = (streak / 86_400_000L).toInt()
                
                StreakHighlightCard(days = days)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Вехи", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    TextButton(onClick = { }) {
                        Text("Смотреть все", color = Color(0xFF006A4E), fontWeight = FontWeight.Bold)
                    }
                }
                MilestonesRow(
                    streakBadges = achievementsState.streakBadges,
                    milestoneBadges = achievementsState.milestoneBadges
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                Text("Настройки", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(16.dp))
                PreferencesSection(
                    onManageHabits = { habits.firstOrNull()?.let { onEditHabit(it.id) } },
                    onPrivacyClick = { showPrivacyDialog = true },
                    settingsViewModel = settingsViewModel,
                    notificationPermissionLauncher = notificationPermissionLauncher
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onNavigateToProfileSelect,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC53030)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7D7))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сменить профиль", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(name: String, level: Int, points: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(
                text = "Создатель устойчивости • С янв 2024",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "УРОВЕНЬ $level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006A4E))
                Text(
                    text = "%,d".format(points) + " Всего баллов",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006A4E)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (points % 500).toFloat() / 500f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = Color(0xFF10B981),
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun StreakHighlightCard(days: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE69100))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.LocalFireDepartment, null, tint = Color.White, modifier = Modifier.size(40.dp))
            Text(
                text = days.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "ДНЕВНАЯ СЕРИЯ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun MilestonesRow(
    streakBadges: List<com.svoboden.app.ui.screens.achievements.BadgeUiModel>,
    milestoneBadges: List<com.svoboden.app.ui.screens.achievements.BadgeUiModel>
) {
    val allBadges = (streakBadges + milestoneBadges).take(4)
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(allBadges.size) { index ->
            val item = allBadges[index]
            Card(
                modifier = Modifier.width(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (item.isUnlocked) Color(0xFFEBF8FF) else Color(0xFFF8FAFC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = null, 
                            tint = if (item.isUnlocked) Color(0xFF3182CE) else Color(0xFF94A3B8), 
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (!item.isUnlocked) Color(0xFF94A3B8) else Color(0xFF1E293B),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PreferencesSection(
    onManageHabits: () -> Unit,
    onPrivacyClick: () -> Unit,
    settingsViewModel: SettingsViewModel,
    notificationPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    val reminderEnabled by settingsViewModel.reminderEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            PreferenceItem(
                title = "Управление привычками",
                subtitle = "Обновите цели и расписания",
                icon = Icons.Default.EditCalendar,
                iconColor = Color(0xFF10B981),
                onClick = onManageHabits
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
            PreferenceItem(
                title = "Уведомления",
                subtitle = "Ежедневные напоминания и оповещения",
                icon = Icons.Default.NotificationsNone,
                iconColor = Color(0xFF3B82F6),
                trailing = { 
                    Switch(
                        checked = reminderEnabled, 
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= 33 &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            settingsViewModel.toggleReminder(enabled, 20, 0)
                        }
                    ) 
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
            PreferenceItem(
                title = "Конфиденциальность и данные",
                subtitle = "Защита, экспорт и импорт данных",
                icon = Icons.Default.Shield,
                iconColor = Color(0xFF64748B),
                onClick = onPrivacyClick
            )
        }
    }
}

@Composable
fun PrivacySettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel,
    onShowUnavailable: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val biometricEnabled by viewModel.biometricLockEnabled.collectAsStateWithLifecycle()
    val timeoutSeconds by viewModel.lockTimeoutSeconds.collectAsStateWithLifecycle()
    val status = remember { viewModel.biometricStatus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Конфиденциальность") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Блокировка приложения", fontWeight = FontWeight.Bold)
                        Text(
                            text = when (status) {
                                BiometricAvailability.Status.AVAILABLE -> "Биометрия или PIN"
                                else -> "Настройте защиту в системе"
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Switch(
                        checked = biometricEnabled,
                        enabled = status == BiometricAvailability.Status.AVAILABLE,
                        onCheckedChange = { 
                            if (status != BiometricAvailability.Status.AVAILABLE) onShowUnavailable()
                            else viewModel.toggleBiometricLock(it)
                        }
                    )
                }
                
                if (biometricEnabled) {
                    Text("Таймаут блокировки", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Сразу", 30 to "30с", 300 to "5м").forEach { (sec, label) ->
                            FilterChip(
                                selected = timeoutSeconds == sec,
                                onClick = { viewModel.setLockTimeout(sec) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
                
                HorizontalDivider()
                
                TextButton(onClick = { 
                    onExportClick()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Upload, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Экспортировать JSON")
                }
                TextButton(onClick = { 
                    onImportClick()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Download, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Импортировать JSON")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Закрыть") } }
    )
}

@Composable
fun PreferenceItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFCBD5E1))
        }
    }
}
