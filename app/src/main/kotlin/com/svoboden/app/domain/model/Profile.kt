package com.svoboden.app.domain.model

/** Семейный профиль (глава 9). Каждая привычка/запись принадлежит ровно одному Profile. */
data class Profile(
    val id: Long = 0,
    val name: String,
    val avatarColor: String,       // hex-строка, напр. "#2E7D32"
    val pinHash: String? = null,   // опциональный PIN для переключения
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false,
)
