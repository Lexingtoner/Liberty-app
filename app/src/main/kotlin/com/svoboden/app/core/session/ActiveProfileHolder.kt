package com.svoboden.app.core.session

import com.svoboden.app.data.local.dao.ProfileDao
import com.svoboden.app.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveProfileHolder @Inject constructor(
    private val profileDao: ProfileDao,
) {
    private val _activeProfileId = MutableStateFlow<Long?>(null)
    val activeProfileId: StateFlow<Long?> = _activeProfileId.asStateFlow()

    /**
     * Вызывать только после того, как БД гарантированно открыта (см. AppInitializer).
     *
     * ФИКС: на чистой установке profiles пуста — profileDao.getActive() и getFirst()
     * оба возвращают null, activeProfileId оставался null навсегда, и любой вызов
     * HabitRepository.saveHabits() (в том числе прямо на экране онбординга) падал
     * с error("Нет активного профиля"). Теперь при отсутствии профилей создаётся
     * профиль по умолчанию — прозрачно для пользователя, до того как семейный режим
     * вообще станет заметен в UI (ProfileSelectScreen показывается только при 2+).
     */
    suspend fun init() {
        val active = profileDao.getActive() ?: profileDao.getFirst()
        if (active != null) {
            _activeProfileId.value = active.id
            return
        }
        val newId = profileDao.insert(
            ProfileEntity(
                name = "Мой профиль",
                avatarColor = "#2E7D32",
                pinHash = null,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )
        )
        _activeProfileId.value = newId
    }

    suspend fun switchTo(profileId: Long) {
        profileDao.clearActive()
        profileDao.setActive(profileId)
        _activeProfileId.value = profileId
    }
}
