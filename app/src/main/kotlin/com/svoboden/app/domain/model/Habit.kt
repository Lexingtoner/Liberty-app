package com.svoboden.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    val id: Long = 0,
    val profileId: Long = 0,
    val type: HabitType,
    val customName: String? = null,        // Только для HabitType.OTHER
    val unit: String,                       // Кастомная единица измерения
    val goalDays: Int? = null,              // null = навсегда
    val createdAt: Long = System.currentTimeMillis()
) {
    val isForever: Boolean get() = goalDays == null
}
