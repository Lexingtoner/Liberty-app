package com.svoboden.app.widget

import android.content.Context
import com.svoboden.app.domain.usecase.StreakResult
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

object HabitWidgetDataSource {
    suspend fun load(context: Context): HabitWidgetState? {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java,
        )
        val habitRepo = entryPoint.habitRepository()
        val calcStreak = entryPoint.calculateStreakUseCase()
        val activeProfileHolder = entryPoint.activeProfileHolder()

        // ФИКС: виджет должен ждать инициализации активного профиля, иначе
        // observeHabits() зависнет на первом элементе filterNotNull() и виджет
        // будет вечно пустым при первой отрисовке до открытия приложения.
        if (activeProfileHolder.activeProfileId.value == null) return null

        val habits = habitRepo.observeHabits().first()
        val habit = habits.firstOrNull() ?: return null

        val streak = calcStreak(habit.id)
        val (days, hours) = if (streak is StreakResult.Active) {
            val totalHours = streak.elapsedMs / 3_600_000L
            (totalHours / 24 to totalHours % 24)
        } else 0L to 0L

        return HabitWidgetState(
            habitName = habit.customName ?: habit.type.displayName,
            unit = habit.unit,
            streakDays = days,
            streakHours = hours,
        )
    }
}
