package com.svoboden.app.data.repository

import com.svoboden.app.core.session.ActiveProfileHolder
import com.svoboden.app.data.local.dao.HabitDao
import com.svoboden.app.data.local.entity.HabitEntity
import com.svoboden.app.domain.model.Habit
import com.svoboden.app.domain.model.HabitType
import com.svoboden.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao,
    private val activeProfileHolder: ActiveProfileHolder
) : HabitRepository {

    override fun observeHabits(): Flow<List<Habit>> =
        activeProfileHolder.activeProfileId
            .filterNotNull()
            .flatMapLatest { profileId -> dao.observeAll(profileId) }
            .map { list -> list.map { it.toDomain() } }

    // ФИКС: теперь возвращает привычки с реальными id из БД (см. HabitDao.insertAll),
    // чтобы вызывающий код (SaveHabitsUseCase) мог создать сессии по верным id
    // вместо id=0 для всех новых привычек.
    override suspend fun saveHabits(habits: List<Habit>): List<Habit> {
        val profileId = activeProfileHolder.activeProfileId.value
            ?: error("Нет активного профиля — saveHabits вызван до инициализации")
        val entities = habits.map { it.toEntity(profileId) }
        val generatedIds = dao.insertAll(entities)
        return habits.mapIndexed { index, habit ->
            // Если привычка уже существовала (id != 0, REPLACE-конфликт по PK),
            // Room всё равно возвращает исходный id, а не новый — это корректно.
            val resultId = if (habit.id != 0L) habit.id else generatedIds[index]
            habit.copy(id = resultId, profileId = profileId)
        }
    }

    override suspend fun getById(habitId: Long): Habit? = dao.getById(habitId)?.toDomain()

    override suspend fun deleteHabit(habit: Habit) {
        dao.delete(habit.toEntity(habit.profileId))
    }

    private fun HabitEntity.toDomain() = Habit(
        id = id,
        profileId = profileId,
        type = HabitType.valueOf(type),
        customName = customName,
        unit = unit,
        goalDays = goalDays,
        createdAt = createdAt
    )

    private fun Habit.toEntity(profileId: Long) = HabitEntity(
        id = id,
        profileId = profileId,
        type = type.name,
        customName = customName,
        unit = unit,
        goalDays = goalDays,
        createdAt = createdAt
    )
}
