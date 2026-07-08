package com.svoboden.app.data.repository

import com.svoboden.app.data.local.dao.ProfileDao
import com.svoboden.app.data.local.entity.ProfileEntity
import com.svoboden.app.domain.model.Profile
import com.svoboden.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao
) : ProfileRepository {

    override fun observeAll(): Flow<List<Profile>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }


    override suspend fun getActive(): Profile? = dao.getActive()?.toDomain()
    override suspend fun getFirst(): Profile? = dao.getFirst()?.toDomain()

    override suspend fun create(profile: Profile): Long {
        val isFirst = dao.count() == 0
        return dao.insert(profile.toEntity().copy(isActive = isFirst))
    }

    override suspend fun setActive(profileId: Long) {
        dao.clearActive()
        dao.setActive(profileId)
    }

    override suspend fun delete(profileId: Long) = dao.delete(profileId)
    override suspend fun count(): Int = dao.count()

    private fun ProfileEntity.toDomain() = Profile(id, name, avatarColor, pinHash, createdAt, isActive)
    private fun Profile.toEntity() = ProfileEntity(id, name, avatarColor, pinHash, createdAt, isActive)
}
