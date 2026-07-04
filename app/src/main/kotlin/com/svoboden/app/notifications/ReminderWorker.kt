package com.svoboden.app.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svoboden.app.MainActivity
import com.svoboden.app.SvobodenApp
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.repository.JournalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitRepo: HabitRepository,
    private val journalRepo: JournalRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habits = habitRepo.observeHabits().first()
        if (habits.isEmpty()) return Result.success()

        val startOfDay = startOfTodayMillis()
        val now = System.currentTimeMillis()
        val unloggedHabits = habits.filter { habit ->
            journalRepo.getEntriesInRange(habit.id, startOfDay, now).isEmpty()
        }
        if (unloggedHabits.isEmpty()) return Result.success()

        showNotification(unloggedHabits.size, unloggedHabits.firstOrNull()?.type?.displayName)
        return Result.success()
    }

    private fun showNotification(count: Int, firstHabitName: String?) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val text = if (count == 1) {
            "Не забудьте отметить сегодняшний день — $firstHabitName"
        } else {
            "У вас $count привычки ждут отметки за сегодня"
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(EXTRA_DESTINATION, DESTINATION_JOURNAL)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, SvobodenApp.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: заменить на кастомную иконку @drawable/ic_notification
            .setContentTitle("Как прошёл день?")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    private fun startOfTodayMillis(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    companion object {
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "daily_reminder"
        const val EXTRA_DESTINATION = "destination"
        const val DESTINATION_JOURNAL = "journal"
    }
}
