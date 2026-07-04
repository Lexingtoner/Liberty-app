package com.svoboden.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "unlocked_achievements",
    foreignKeys = [ForeignKey(
        entity = HabitEntity::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("habitId"), Index(value = ["habitId", "achievementKey"], unique = true)]
)
data class UnlockedAchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val achievementKey: String,
    val unlockedAt: Long
)
