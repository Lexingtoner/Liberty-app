package com.svoboden.app.data.local.dao

import androidx.room.*
import com.svoboden.app.data.local.entity.TriggerTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerTemplateDao {
    @Query("SELECT * FROM trigger_templates")
    fun observeAll(): Flow<List<TriggerTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(templates: List<TriggerTemplateEntity>)
}
