package com.svoboden.app.data.repository

import com.svoboden.app.data.local.dao.UserProfileDao
import com.svoboden.app.data.local.entity.UserProfileEntity
import com.svoboden.app.domain.model.ThemeMode
import com.svoboden.app.domain.model.UserProfile
import com.svoboden.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl @javax.inject.Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> =
        dao.observe().map { it?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    // ФИКС: раньше completeOnboarding() безусловно перезаписывал motivation
    // пустой строкой, стирая то, что пользователь мог ввести на онбординге,
    // если saveProfile() и completeOnboarding() вызывались в разном порядке.
    // Теперь метод читает текущее состояние и меняет только один флаг.
    override suspend fun completeOnboarding() {
        val current = dao.observe().first()?.toDomain() ?: UserProfile()
        dao.upsert(current.copy(onboardingCompleted = true).toEntity())
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        id = id,
        motivation = motivation,
        onboardingCompleted = onboardingCompleted,
        themeMode = ThemeMode.valueOf(themeMode)
    )

    private fun UserProfile.toEntity() = UserProfileEntity(
        id = id,
        motivation = motivation,
        onboardingCompleted = onboardingCompleted,
        themeMode = themeMode.name
    )
}
