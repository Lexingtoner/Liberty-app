package com.svoboden.app.data.repository

import com.svoboden.app.data.local.dao.SessionDao
import com.svoboden.app.data.local.entity.SessionEntity
import com.svoboden.app.domain.model.StartSession
import com.svoboden.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao
) : SessionRepository {

    override suspend fun getActiveSession(habitId: Long): StartSession? =
        dao.getActiveSession(habitId)?.toDomain()

    override suspend fun startSession(habitId: Long, startedAt: Long) {
        dao.insert(SessionEntity(habitId = habitId, startedAt = startedAt, restartedAt = null, isActive = true))
    }

    override suspend fun closeActiveSession(habitId: Long, closedAt: Long) =
        dao.closeActiveSession(habitId, closedAt)

    override fun observeByHabit(habitId: Long): Flow<List<StartSession>> =
        dao.observeByHabit(habitId).map { list -> list.map { it.toDomain() } }

    private fun SessionEntity.toDomain() = StartSession(id, habitId, startedAt, restartedAt, isActive)
}
