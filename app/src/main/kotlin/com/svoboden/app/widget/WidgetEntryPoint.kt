package com.svoboden.app.widget

import com.svoboden.app.core.session.ActiveProfileHolder
import com.svoboden.app.domain.repository.HabitRepository
import com.svoboden.app.domain.usecase.CalculateStreakUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * GlanceAppWidget не поддерживает @AndroidEntryPoint — доступ к Hilt-графу
 * получаем через EntryPoint, это стандартный паттерн для Glance + Hilt.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun habitRepository(): HabitRepository
    fun calculateStreakUseCase(): CalculateStreakUseCase
    fun activeProfileHolder(): ActiveProfileHolder
}
