package com.svoboden.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1,
    val motivation: String,
    val onboardingCompleted: Boolean,
    val themeMode: String // ThemeMode.name
)
