package com.svoboden.app.ui.screens.error

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DatabaseErrorScreen(onResetData: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
            Text("Не удалось открыть данные", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Это может произойти после сброса настроек устройства. К сожалению, зашифрованные данные без ключа восстановить нельзя — так мы защищаем вашу приватность.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onResetData) { Text("Начать заново") }
        }
    }
}
