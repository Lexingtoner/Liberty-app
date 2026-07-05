package com.svoboden.app.core.utils

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.svoboden.app.widget.HabitWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun updateAll() {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(HabitWidget::class.java)
        glanceIds.forEach { id ->
            HabitWidget().update(context, id)
        }
    }
}
