package com.svoboden.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("profileId")]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val type: String,          // HabitType.name
    val customName: String?,
    val unit: String,
    val goalDays: Int?,
    val createdAt: Long
)
