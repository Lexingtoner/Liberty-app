package com.svoboden.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Новый файл: шкала форм Material 3. До этого radius-значения (12.dp, 16.dp,
// 20.dp) были разбросаны литералами по каждому экрану отдельно — единой
// системы не было, из-за чего скругления на разных экранах могли незаметно
// разъехаться. Теперь MaterialTheme.shapes.* — единый источник, и Card/Button/
// Dialog по умолчанию берут форму отсюда без explicit shape = RoundedCornerShape(...).
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
