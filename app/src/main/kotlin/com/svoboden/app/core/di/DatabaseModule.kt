package com.svoboden.app.core.di

import android.content.Context
import androidx.room.Room
import com.svoboden.app.core.security.DatabaseKeyProvider
import com.svoboden.app.data.local.ALL_MIGRATIONS
import com.svoboden.app.data.local.AppDatabase
import com.svoboden.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext ctx: Context,
        keyProvider: DatabaseKeyProvider
    ): AppDatabase {
        SQLiteDatabase.loadLibs(ctx)
        val passphrase = keyProvider.getOrCreatePassphrase()
        // ФИКС: правильное имя класса — net.sqlcipher.database.SupportFactory.
        // "SupportOpenHelperFactory" из чернового плана не существует в этой библиотеке.
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(ctx, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .openHelperFactory(factory)
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }

    @Provides fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
    @Provides fun provideJournalDao(db: AppDatabase): JournalEntryDao = db.journalEntryDao()
    @Provides fun provideTriggerDao(db: AppDatabase): TriggerTemplateDao = db.triggerTemplateDao()
    @Provides fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()
    @Provides fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()
}
