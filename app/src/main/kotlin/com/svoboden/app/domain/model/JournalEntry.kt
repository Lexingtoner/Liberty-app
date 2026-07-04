package com.svoboden.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val id: Long = 0,
    val habitId: Long,
    val date: Long,
    val hadRelapse: Boolean,
    val triggerId: Long? = null,
    val customNote: String? = null,
    val mood: Int? = null // 1..5, опционально
)
