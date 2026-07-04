package com.svoboden.app.data.local.dao

import androidx.room.*
import com.svoboden.app.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE profileId = :profileId ORDER BY createdAt ASC")
    fun observeAll(profileId: Long): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getById(habitId: Long): HabitEntity?

    // ФИКС: раньше был `suspend fun insertAll(habits: List<HabitEntity>)` без возврата id.
    // SaveHabitsUseCase затем пытался создать StartSession по habit.id, который
    // для новых привычек оставался 0 (дефолт data class) — все новые привычки
    // получали одну и ту же "сессию с habitId=0", что нарушает FK и логику стриков.
    // Room @Insert поддерживает возврат List<Long> сгенерированных rowId.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<HabitEntity>): List<Long>

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)
}
