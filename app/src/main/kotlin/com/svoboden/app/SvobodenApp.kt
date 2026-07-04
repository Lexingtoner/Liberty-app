package com.svoboden.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SvobodenApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // ПРИМЕЧАНИЕ: AppLockManager.attachTo() намеренно НЕ вызывается здесь —
        // см. ReadyAppContent в MainActivity и комментарий в AppInitializer:
        // подписку на ProcessLifecycleOwner нужно начинать только после Ready,
        // иначе возможен race condition при долгой миграции шифрования на первом запуске.
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_REMINDERS, "Ежедневные напоминания", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Напоминания отметить день в дневнике" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_REMINDERS = "channel_reminders"
    }
}
