package com.svoboden.app.core.utils

import android.content.Context
import com.svoboden.app.widget.HabitWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun updateAll() {
        HabitWidget().updateAll(context)
    }
}
