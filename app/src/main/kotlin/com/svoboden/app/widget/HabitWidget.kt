package com.svoboden.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.svoboden.app.MainActivity

class HabitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = HabitWidgetDataSource.load(context)
        provideContent {
            HabitWidgetContent(state)
        }
    }
}

@Composable
private fun HabitWidgetContent(state: HabitWidgetState?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(androidx.glance.color.ColorProvider(androidx.compose.ui.graphics.Color(0xFFC8E6C9)))
            .cornerRadius(20.dp)
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        if (state == null) {
            Text(text = "Добавьте привычку в приложении")
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = state.habitName, style = TextStyle(fontSize = 14.sp))
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = "${state.streakDays} дн. ${state.streakHours} ч.",
                    style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(GlanceModifier.height(2.dp))
                Text(text = "без ${state.unit}", style = TextStyle(fontSize = 12.sp))
            }
        }
    }
}
