package com.svoboden.app.ui.theme

import androidx.compose.ui.graphics.Color

// Базовая палитра (использовалась в исходной сокращённой теме — оставлена
// для обратной совместимости на случай, если где-то ещё ссылаются напрямую)
val GreenPrimary = Color(0xFF006A4E)
val AmberAccent = Color(0xFFE69100)
val BackgroundDark = Color(0xFF121A12)
val BackgroundLight = Color(0xFFF8FAFC)
val DarkGreenCard = Color(0xFF004D39)

// ФИКС относительно прошлой версии: MaterialTheme.colorScheme использует role'ы
// error/outline/surfaceVariant/onSurfaceVariant повсеместно в стандартных
// компонентах (OutlinedTextField, HorizontalDivider, disabled-состояния и т.д.).
// Если их не задать явно, Compose подставляет дефолтные Material-фиолетовые
// тона, которые визуально ломают зелёную айдентику приложения. Ниже — полная
// палитра Material 3 (Light + Dark), сгенерированная вокруг GreenPrimary.

val md_light_primary = GreenPrimary
val md_light_onPrimary = Color(0xFFFFFFFF)
val md_light_primaryContainer = Color(0xFFC8E6C9)
val md_light_onPrimaryContainer = Color(0xFF002106)
val md_light_secondary = AmberAccent
val md_light_onSecondary = Color(0xFF442B00)
val md_light_secondaryContainer = Color(0xFFFFDDB1)
val md_light_onSecondaryContainer = Color(0xFF2A1800)
val md_light_tertiary = Color(0xFF1D6D5A)
val md_light_onTertiary = Color(0xFFFFFFFF)
val md_light_tertiaryContainer = Color(0xFFA0F2D7)
val md_light_onTertiaryContainer = Color(0xFF00201A)
val md_light_error = Color(0xFFBA1A1A)
val md_light_onError = Color(0xFFFFFFFF)
val md_light_errorContainer = Color(0xFFFFDAD6)
val md_light_onErrorContainer = Color(0xFF410002)
val md_light_background = Color(0xFFF6FAF3)
val md_light_onBackground = Color(0xFF181D17)
val md_light_surface = Color(0xFFF6FAF3)
val md_light_onSurface = Color(0xFF181D17)
val md_light_surfaceVariant = Color(0xFFDEE5D8)
val md_light_onSurfaceVariant = Color(0xFF424940)
val md_light_outline = Color(0xFF72796F)
val md_light_outlineVariant = Color(0xFFC2C9BC)

val md_dark_primary = Color(0xFF8FD98A)
val md_dark_onPrimary = Color(0xFF00390A)
val md_dark_primaryContainer = Color(0xFF1B5E20)
val md_dark_onPrimaryContainer = Color(0xFFAAF3A5)
val md_dark_secondary = Color(0xFFFBBF24)
val md_dark_onSecondary = Color(0xFF452B00)
val md_dark_secondaryContainer = Color(0xFF624000)
val md_dark_onSecondaryContainer = Color(0xFFFFDDB1)
val md_dark_tertiary = Color(0xFF84D5BE)
val md_dark_onTertiary = Color(0xFF00382C)
val md_dark_tertiaryContainer = Color(0xFF005141)
val md_dark_onTertiaryContainer = Color(0xFFA0F2D7)
val md_dark_error = Color(0xFFFFB4AB)
val md_dark_onError = Color(0xFF690005)
val md_dark_errorContainer = Color(0xFF93000A)
val md_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_dark_background = BackgroundDark
val md_dark_onBackground = Color(0xFFE1E3DD)
val md_dark_surface = Color(0xFF10140F)
val md_dark_onSurface = Color(0xFFE1E3DD)
val md_dark_surfaceVariant = Color(0xFF424940)
val md_dark_onSurfaceVariant = Color(0xFFC2C9BC)
val md_dark_outline = Color(0xFF8C9388)
val md_dark_outlineVariant = Color(0xFF424940)
