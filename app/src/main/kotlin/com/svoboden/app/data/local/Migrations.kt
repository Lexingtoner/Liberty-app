package com.svoboden.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * СВОДНЫЙ ФАЙЛ МИГРАЦИЙ — единая точка правды вместо миграций,
 * разбросанных по главам 6, 8 и 9. Порядок версий:
 *
 *   v1 — исходная MVP-схема (habits, sessions, journal_entries,
 *        trigger_templates, user_profile)
 *   v2 — + unlocked_achievements (достижения, глава 6)
 *   v3 — + profiles, + habits.profileId (семейный режим, глава 9)
 *
 * ВАЖНО про шифрование (глава 8): миграции ничего не знают о SQLCipher —
 * к моменту, когда Room вызывает migrate(), файл БД уже открыт по верному
 * паролю через SupportFactory в DatabaseModule. Расшифровка и Room-миграции
 * — независимые уровни, их нельзя перепутывать местами по ответственности.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS unlocked_achievements (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                habitId INTEGER NOT NULL,
                achievementKey TEXT NOT NULL,
                unlockedAt INTEGER NOT NULL,
                FOREIGN KEY(habitId) REFERENCES habits(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_unlocked_achievements_habitId " +
                "ON unlocked_achievements(habitId)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_unlocked_achievements_habitId_achievementKey " +
                "ON unlocked_achievements(habitId, achievementKey)"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS profiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                avatarColor TEXT NOT NULL,
                pinHash TEXT,
                createdAt INTEGER NOT NULL,
                isActive INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Профиль по умолчанию для тех, кто обновляется с версии без семейного режима —
        // все существующие привычки и записи ниже привязываются именно к нему.
        db.execSQL(
            """
            INSERT INTO profiles (id, name, avatarColor, pinHash, createdAt, isActive)
            VALUES (1, 'Мой профиль', '#2E7D32', NULL, $now, 1)
            """.trimIndent()
        )

        // ФИКС относительно чернового плана главы 9: profileId добавляется
        // ТОЛЬКО в habits. Таблицы journal_entries/sessions/unlocked_achievements
        // не получают собственный profileId — они уже однозначно скопированы
        // на профиль транзитивно через habitId → habits.profileId. Дублирование
        // столбца в четырёх таблицах создавало риск рассинхронизации при вставке
        // (нужно было бы каждый раз явно прокидывать profileId в несколько мест).
        db.execSQL("ALTER TABLE habits ADD COLUMN profileId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_habits_profileId ON habits(profileId)")
    }
}

/** Передаётся в Room.databaseBuilder(...).addMigrations(*ALL_MIGRATIONS). */
val ALL_MIGRATIONS: Array<Migration> = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3
)
