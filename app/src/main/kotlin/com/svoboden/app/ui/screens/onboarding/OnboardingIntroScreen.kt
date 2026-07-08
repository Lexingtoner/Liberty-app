package com.svoboden.app.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private data class IntroPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val accentColor: Color
)

private val introPages = listOf(
    IntroPage(
        icon = Icons.Default.Spa,
        title = "Добро пожаловать в Свободен",
        description = "Ваш личный путь к осознанной жизни начинается здесь. Без осуждения, в вашем собственном темпе.",
        accentColor = Color(0xFF006A4E)
    ),
    IntroPage(
        icon = Icons.Default.CalendarMonth,
        title = "Трекайте свои успехи",
        description = "Отмечайте каждый день чистоты, следите за ростом серий и превращайте усилия в привычку.",
        accentColor = Color(0xFF006A4E)
    ),
    IntroPage(
        icon = Icons.Default.Favorite,
        title = "Срывы — это уроки",
        description = "Мы поможем вам понять триггеры и вернуться в строй без чувства вины. Каждый день — это новый шанс.",
        accentColor = Color(0xFFE69100)
    ),
    IntroPage(
        icon = Icons.Default.Lock,
        title = "Полная приватность",
        description = "Все ваши данные шифруются и хранятся только на этом устройстве. Ваша история — только ваша.",
        accentColor = Color(0xFF3B82F6)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingIntroScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == introPages.lastIndex

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PageIndicator(pagerState = pagerState, pageCount = introPages.size)
                Spacer(Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!isLastPage) {
                        TextButton(
                            onClick = onFinished,
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Text("Пропустить", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    
                    Button(
                        onClick = {
                            if (isLastPage) {
                                onFinished()
                            } else {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLastPage) Color(0xFFE69100) else Color(0xFF006A4E)
                        )
                    ) {
                        Text(
                            text = if (isLastPage) "Начать путь" else "Далее",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { page ->
            IntroPageContent(introPages[page])
        }
    }
}

@Composable
private fun IntroPageContent(page: IntroPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Декоративный элемент с иконкой
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(page.accentColor.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(page.accentColor, page.accentColor.copy(alpha = 0.8f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(Modifier.height(48.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageIndicator(pagerState: PagerState, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            val selected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (selected) 24.dp else 12.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (selected) Color(0xFF006A4E)
                        else Color(0xFFE2E8F0)
                    )
            )
        }
    }
}
