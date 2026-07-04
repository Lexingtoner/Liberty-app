package com.svoboden.app.ui.screens.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svoboden.app.domain.model.Profile

private val palette = listOf("#2E7D32", "#1565C0", "#EF6C00", "#6A1B9A", "#AD1457")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectScreen(onProfileSelected: () -> Unit, viewModel: ProfileSelectViewModel = hiltViewModel()) {
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(value = false) }

    LaunchedEffect(uiState.switched) { if (uiState.switched) onProfileSelected() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Кто продолжает путь?") }) },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(profiles, key = { it.id }) { profile ->
                ProfileCard(profile, onClick = { viewModel.requestSwitch(profile) })
            }
            item { AddProfileCard(onClick = { showCreateDialog = true }) }
        }
    }

    uiState.pendingProfile?.let { profile ->
        PinEntryDialog(profileName = profile.name, error = uiState.pinError, onConfirm = viewModel::confirmPin, onDismiss = viewModel::dismissPinDialog)
    }
    if (showCreateDialog) {
        CreateProfileDialog(
            onCreate = { name, color -> viewModel.createProfile(name, color); showCreateDialog = false },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun ProfileCard(profile: Profile, onClick: () -> Unit) {
    Card(modifier = Modifier.aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(56.dp).clip(CircleShape).background(parseHexColor(profile.avatarColor)), contentAlignment = Alignment.Center) {
                Text(profile.name.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(8.dp))
            Text(profile.name, style = MaterialTheme.typography.titleSmall)
            if (profile.pinHash != null) Icon(Icons.Default.Lock, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AddProfileCard(onClick: () -> Unit) {
    Card(modifier = Modifier.aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.height(4.dp))
            Text("Новый профиль", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun PinEntryDialog(profileName: String, error: String?, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("PIN профиля «$profileName»") },
        text = {
            Column {
                OutlinedTextField(
                    value = pin, onValueChange = { pin = it.filter(Char::isDigit).take(6) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = error != null,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(pin) }) { Text("Войти") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
fun CreateProfileDialog(onCreate: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(palette.first()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый профиль") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Имя") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palette.forEach { hex ->
                        Box(
                            Modifier.size(32.dp).clip(CircleShape).background(parseHexColor(hex))
                                .clickable { color = hex }
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onCreate(name, color) }) { Text("Создать") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private fun parseHexColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Exception) {
    Color(0xFF2E7D32)
}
