package com.svoboden.app.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen() {
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
                        text = "Сообщество",
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
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                CommunityBanner()
            }
            
            item {
                CommunityFilters()
            }
            
            item {
                PostItem(
                    author = "Джеймс Д.",
                    time = "2 часа назад",
                    title = "Как я выжил в первую неделю",
                    content = "Первые 3 дня были самыми трудными. Я постоянно тянулся к телефону, чтобы отвлечься от тяги. Что мне больше всего помогло, так это техника дыхания 4-7-8, когда наступала волна...",
                    likes = 124,
                    comments = 18,
                    tag = "ВЕХА",
                    tagColor = Color(0xFF68D391)
                )
            }
            
            item {
                PostItem(
                    author = "Мария К.",
                    time = "5 часов назад",
                    title = "Советы по управлению стрессом",
                    content = "Стресс часто является самым большим триггером для рецидива. Я начала вести 5-минутный утренний дневник, где записываю три вещи, за которые я благодарна. Это звучит просто, но это меняет весь мой настрой...",
                    likes = 89,
                    comments = 7,
                    tag = "СОВЕТЫ",
                    tagColor = Color(0xFFF6AD55)
                )
            }
        }
    }
}

@Composable
fun CommunityBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF006A4E), Color(0xFF059669))
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Ваш голос важен",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Каждая разделенная веха вдохновляет кого-то другого продолжать путь.",
                fontSize = 14.sp,
                color = Color(0xFFB2F5EA),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF006A4E), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Поделиться прогрессом", color = Color(0xFF006A4E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CommunityFilters() {
    val filters = listOf("Все истории", "Вехи", "Снятие стресса")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 20.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = filter == "Все истории",
                onClick = { },
                label = { Text(filter) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF006A4E),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFE2E8F0),
                    labelColor = Color(0xFF64748B)
                ),
                border = null
            )
        }
    }
}

@Composable
fun PostItem(
    author: String,
    time: String,
    title: String,
    content: String,
    likes: Int,
    comments: Int,
    tag: String,
    tagColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDF2F7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(author.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF4A5568))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = time, fontSize = 12.sp, color = Color(0xFF94A3B8))
                    }
                }
                Surface(
                    color = tagColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color(0xFF475569),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Заглушка для изображения если оно есть (как на скриншоте)
            if (title.contains("первую неделю")) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE2E8F0))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    Text(" $likes", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.ChatBubbleOutline, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    Text(" $comments", fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
                TextButton(onClick = { }) {
                    Text("Читать далее", color = Color(0xFF006A4E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF006A4E), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
