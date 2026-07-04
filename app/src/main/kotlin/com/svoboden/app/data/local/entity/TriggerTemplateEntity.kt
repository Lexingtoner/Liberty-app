package com.svoboden.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trigger_templates")
data class TriggerTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val isCustom: Boolean
)
