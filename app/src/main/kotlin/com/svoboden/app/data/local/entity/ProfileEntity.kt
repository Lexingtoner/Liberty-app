package com.svoboden.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarColor: String,
    val pinHash: String?,
    val createdAt: Long,
    val isActive: Boolean
)
