package com.svoboden.app.ui.screens.lock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.svoboden.app.core.security.BiometricAuthenticator

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun LockScreen(onUnlocked: () -> Unit) {
    val activity = LocalContext.current as FragmentActivity
    val authenticator = remember { BiometricAuthenticator(activity) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authenticator.authenticate(onSuccess = onUnlocked, onError = { errorMessage = it }, onCancel = { })
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.Lock, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
            Text("Приложение заблокировано", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Ваш путь — только ваш. Подтвердите личность, чтобы продолжить.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            errorMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
            Button(onClick = {
                errorMessage = null
                authenticator.authenticate(onSuccess = onUnlocked, onError = { errorMessage = it }, onCancel = { })
            }) {
                Icon(Icons.Default.Fingerprint, null)
                Spacer(Modifier.width(8.dp))
                Text("Попробовать снова")
            }
        }
    }
}
