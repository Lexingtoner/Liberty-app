package com.svoboden.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Формат файла резервной копии (глава 4).
 * ОГРАНИЧЕНИЕ: экспортируются данные только активного профиля (глава 9)
 * — резервная копия не является полным дампом всех семейных профилей.
 * Это осознанное сужение MVP-скоупа, чтобы не путать пользователя, чей это бэкап.
 */
@Serializable
data class ExportData(
    val exportedAt: Long,
    val schemaVersion: Int = 1,
    val habits: List<Habit>,
    val entries: List<JournalEntry>
)
