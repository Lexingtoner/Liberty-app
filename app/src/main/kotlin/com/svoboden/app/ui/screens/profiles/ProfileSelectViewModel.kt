package com.svoboden.app.ui.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svoboden.app.core.security.ProfilePinHasher
import com.svoboden.app.core.session.ActiveProfileHolder
import com.svoboden.app.domain.model.Profile
import com.svoboden.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSelectUiState(
    val pendingProfile: Profile? = null,
    val pinError: String? = null,
    val switched: Boolean = false
)

@HiltViewModel
class ProfileSelectViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val activeProfileHolder: ActiveProfileHolder
) : ViewModel() {

    val profiles = profileRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(ProfileSelectUiState())
    val uiState: StateFlow<ProfileSelectUiState> = _uiState.asStateFlow()

    fun requestSwitch(profile: Profile) {
        if (profile.pinHash != null) {
            _uiState.update { it.copy(pendingProfile = profile, pinError = null) }
        } else {
            switchNow(profile.id)
        }
    }

    fun confirmPin(pin: String) {
        val pending = _uiState.value.pendingProfile ?: return
        if (ProfilePinHasher.verify(pin, pending.pinHash!!)) {
            switchNow(pending.id)
        } else {
            _uiState.update { it.copy(pinError = "Неверный PIN") }
        }
    }

    fun dismissPinDialog() = _uiState.update { it.copy(pendingProfile = null, pinError = null) }

    private fun switchNow(profileId: Long) = viewModelScope.launch {
        activeProfileHolder.switchTo(profileId)
        _uiState.update { it.copy(pendingProfile = null, pinError = null, switched = true) }
    }

    fun createProfile(name: String, color: String) = viewModelScope.launch {
        profileRepo.create(Profile(name = name, avatarColor = color))
    }
}
