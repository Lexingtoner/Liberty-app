package com.svoboden.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.svoboden.app.data.local.dao.*
import com.svoboden.app.data.local.entity.*

@Database(
    entities = [
        HabitEntity::class,
        SessionEntity::class,
        JournalEntryEntity::class,
        TriggerTemplateEntity::class,
        UserProfileEntity::class,
        UnlockedAchievementEntity::class,
        ProfileEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun sessionDao(): SessionDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun triggerTemplateDao(): TriggerTemplateDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun profileDao(): ProfileDao

    companion object {
        const val DATABASE_NAME = "svoboden_db"
    }
}
