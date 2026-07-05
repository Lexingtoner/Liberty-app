package com.svoboden.app.ui.screens.profile

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
import androidx.compose.runtime.Composable
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
import com.svoboden.app.ui.screens.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToProfileSelect: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
            // Карточка профиля
            item {
                UserInfoCard(name = "Алекс Морган", level = 14, points = 2450)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Выделение стрика
            item {
                StreakHighlightCard(days = 12)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Вехи (Milestones)
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
                MilestonesRow()
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Настройки (Preferences)
            item {
                Text("Настройки", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(16.dp))
                PreferencesSection(onNavigateToProfileSelect = onNavigateToProfileSelect)
            }
            
            // Выход
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
                progress = { 0.6f },
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
fun MilestonesRow() {
    val milestones = listOf(
        MilestoneItem("7 Days Hero", Icons.Default.EmojiEvents, Color(0xFFEBF8FF), Color(0xFF3182CE)),
        MilestoneItem("Financial Master", Icons.Default.AccountBalanceWallet, Color(0xFFF0FFF4), Color(0xFF38A169)),
        MilestoneItem("Deep Breather", Icons.Default.Air, Color(0xFFFFF5F5), Color(0xFFE53E3E)),
        MilestoneItem("30 Days...", Icons.Default.Lock, Color(0xFFF8FAFC), Color(0xFF94A3B8), isLocked = true)
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(milestones.size) { index ->
            val item = milestones[index]
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
                            .background(item.bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, null, tint = item.iconColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (item.isLocked) Color(0xFF94A3B8) else Color(0xFF1E293B),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

data class MilestoneItem(
    val title: String,
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color,
    val isLocked: Boolean = false
)

@Composable
fun PreferencesSection(onNavigateToProfileSelect: () -> Unit) {
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
                iconColor = Color(0xFF10B981)
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
            PreferenceItem(
                title = "Уведомления",
                subtitle = "Ежедневные напоминания и оповещения",
                icon = Icons.Default.NotificationsNone,
                iconColor = Color(0xFF3B82F6),
                trailing = { Switch(checked = true, onCheckedChange = { }) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
            PreferenceItem(
                title = "Конфиденциальность и безопасность",
                subtitle = "Экспорт данных и безопасность аккаунта",
                icon = Icons.Default.Shield,
                iconColor = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun PreferenceItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
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
