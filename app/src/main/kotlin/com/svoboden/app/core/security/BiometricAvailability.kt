package com.svoboden.app.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricViewModel
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricAvailability @Inject constructor(
    @ApplicationContext private val context: Context
) {
    enum class Status { AVAILABLE, NO_HARDWARE, NOT_ENROLLED, UNAVAILABLE }

    fun check(): Status {
        val context = context.applicationContext
        if (!context.packageManager.hasSystemFeature("android.hardware.biometrics")) {
            return Status.NO_HARDWARE
        }
        val manager = BiometricManager.from(context)
        return when (
            manager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> Status.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Status.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Status.NOT_ENROLLED
            else -> Status.UNAVAILABLE
        }
    }
}
