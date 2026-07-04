package com.svoboden.app.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(viewModel: AchievementsViewModel = hiltViewModel()) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habits) {
        if (uiState.selectedHabitId == null) habits.firstOrNull()?.let { viewModel.selectHabit(it.id) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Достижения") }) },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (habits.size > 1) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(habits) { habit ->
                            FilterChip(selected = habit.id == uiState.selectedHabitId, onClick = { viewModel.selectHabit(habit.id) }, label = { Text(habit.type.displayName) })
                        }
                    }
                }
            }
            val unlockedCount = uiState.streakBadges.count { it.isUnlocked } + uiState.milestoneBadges.count { it.isUnlocked }
            val totalCount = uiState.streakBadges.size + uiState.milestoneBadges.size
            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Открыто наград", style = MaterialTheme.typography.bodyMedium)
                        Text("$unlockedCount / $totalCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            item { Text("За стрик", style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(8.dp)); BadgeGrid(uiState.streakBadges) }
            item { Text("Особые вехи", style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(8.dp)); BadgeGrid(uiState.milestoneBadges) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BadgeGrid(badges: List<BadgeUiModel>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        badges.forEach { badge -> BadgeItem(badge, Modifier.width(100.dp)) }
    }
}

@Composable
fun BadgeItem(badge: BadgeUiModel, modifier: Modifier = Modifier) {
    val alpha = if (badge.isUnlocked) 1f else 0.35f
    val containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(containerColor).alpha(alpha), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = badgeIconFor(badge.key),
                contentDescription = badge.title,
                modifier = Modifier.size(32.dp),
                tint = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!badge.isUnlocked) Icon(Icons.Default.Lock, null, modifier = Modifier.size(16.dp).align(Alignment.BottomEnd), tint = MaterialTheme.colorScheme.outline)
        }
        Text(badge.title, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2,
            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun badgeIconFor(key: String): ImageVector = when {
    key.contains("FIRST") -> Icons.Default.Star
    key.contains("WEEK") -> Icons.Default.LocalFlorist
    key.contains("MONTH") -> Icons.Default.Park
    key.contains("QUARTER") -> Icons.Default.Forest
    key.contains("HALF") -> Icons.Default.Terrain
    key.contains("YEAR") -> Icons.Default.EmojiEvents
    key.contains("HONEST") -> Icons.Default.Favorite
    key.contains("COMEBACK") -> Icons.Default.Refresh
    key.contains("MULTI") -> Icons.Default.Shield
    else -> Icons.Default.MilitaryTech
}
