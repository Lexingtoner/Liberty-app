package com.svoboden.app.ui.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.core.security.BiometricAvailability
import com.svoboden.app.domain.model.ThemeMode
import com.svoboden.app.domain.usecase.ImportResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToProfiles: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val reminderEnabled by viewModel.reminderEnabled.collectAsStateWithLifecycle()
    val biometricEnabled by viewModel.biometricLockEnabled.collectAsStateWithLifecycle()
    val timeoutSeconds by viewModel.lockTimeoutSeconds.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showUnavailableDialog by remember { mutableStateOf(value = false) }
    var showImportWarning by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { viewModel.exportData(it, context) }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.importData(it, context) }
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        viewModel.exportResult.collect { result ->
            val msg = when (result) {
                is ExportResult.Success -> "Данные экспортированы"
                is ExportResult.Error -> result.msg
            }
            snackbarHostState.showSnackbar(msg)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.importResult.collect { result ->
            val msg = when (result) {
                is ImportResult.Success -> "Импортировано: ${result.habitsCount} привычек, ${result.entriesCount} записей"
                is ImportResult.Error -> result.message
            }
            snackbarHostState.showSnackbar(msg)
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
            text = { Text("Сначала настройте блокировку экрана (отпечаток, лицо или PIN) в настройках устройства.") },
            confirmButton = { TextButton(onClick = { showUnavailableDialog = false }) { Text("Понятно") } }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = { TopAppBar(title = { Text("Настройки") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsSection("Оформление") {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            Modifier.fillMaxWidth().clickable { viewModel.changeTheme(mode) }.padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(themeModeLabel(mode))
                            if (profile?.themeMode == mode) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    // Material You доступен только с Android 12 (API 31) — динамические
                    // цвета из системных обоев вместо фирменной зелёной палитры.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsStateWithLifecycle()
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Цвета из обоев") },
                            supportingContent = { Text("Material You — адаптирует палитру под систему") },
                            trailingContent = {
                                Switch(checked = dynamicColorEnabled, onCheckedChange = viewModel::toggleDynamicColor)
                            }
                        )
                    }
                }
            }

            item {
                SettingsSection("Уведомления") {
                    ListItem(
                        headlineContent = { Text("Ежедневное напоминание") },
                        supportingContent = { Text("Напомним, если забудете отметить день") },
                        trailingContent = {
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= 33 &&
                                        (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                                    ) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    viewModel.toggleReminder(enabled, hour = 20, minute = 0)
                                }
                            )
                        }
                    )
                }
            }

            item {
                SettingsSection("Конфиденциальность") {
                    val status = remember { viewModel.biometricStatus() }
                    ListItem(
                        headlineContent = { Text("Блокировка приложения") },
                        supportingContent = {
                            Text(when (status) {
                                BiometricAvailability.Status.AVAILABLE -> "Отпечаток, лицо или PIN устройства"
                                BiometricAvailability.Status.NOT_ENROLLED -> "Настройте блокировку экрана в системе"
                                else -> "Недоступно на этом устройстве"
                            })
                        },
                        trailingContent = {
                            Switch(
                                checked = biometricEnabled,
                                enabled = status == BiometricAvailability.Status.AVAILABLE,
                                onCheckedChange = { enabled ->
                                    if (status != BiometricAvailability.Status.AVAILABLE) showUnavailableDialog = true
                                    else viewModel.toggleBiometricLock(enabled)
                                }
                            )
                        }
                    )
                    if (biometricEnabled) {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0 to "Сразу", 30 to "30 сек", 300 to "5 мин").forEach { (seconds, label) ->
                                FilterChip(selected = timeoutSeconds == seconds, onClick = { viewModel.setLockTimeout(seconds) }, label = { Text(label) })
                            }
                        }
                    }
                }
            }

            item {
                SettingsSection("Данные") {
                    ListItem(
                        headlineContent = { Text("Экспортировать данные") },
                        supportingContent = { Text("Файл сохраняется без шифрования — храните его в надёжном месте") },
                        trailingContent = { Icon(Icons.Default.Upload, "Экспорт") },
                        modifier = Modifier.clickable { exportLauncher.launch("svoboden_backup.json") }
                    )
                    ListItem(
                        headlineContent = { Text("Импортировать данные") },
                        supportingContent = { Text("Восстановить из ранее сохранённого файла") },
                        trailingContent = { Icon(Icons.Default.Download, "Импорт") },
                        modifier = Modifier.clickable { showImportWarning = true }
                    )
                    ListItem(
                        headlineContent = { Text("Сменить профиль") },
                        supportingContent = { Text("Семейный режим — переключение между профилями") },
                        trailingContent = { Icon(Icons.Default.SwitchAccount, null) },
                        modifier = Modifier.clickable(onClick = onNavigateToProfiles)
                    )
                }
            }

            item {
                Text("Все данные хранятся только на вашем устройстве.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp))
            content()
        }
    }
}

fun themeModeLabel(mode: ThemeMode) = when (mode) {
    ThemeMode.LIGHT -> "Светлая"
    ThemeMode.DARK -> "Тёмная"
    ThemeMode.SYSTEM -> "Системная"
}
