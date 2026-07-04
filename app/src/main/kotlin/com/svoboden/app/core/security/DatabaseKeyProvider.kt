package com.svoboden.app.core.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хранит passphrase для SQLCipher внутри EncryptedSharedPreferences
 * (сам файл шифруется ключом из Android Keystore — passphrase БД никогда
 * не хранится и не передаётся в открытом виде).
 */
@Singleton
class DatabaseKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "svoboden_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getOrCreatePassphrase(): ByteArray {
        val existing = encryptedPrefs.getString(KEY_DB_PASSPHRASE, null)
        if (existing != null) {
            return Base64.decode(existing, Base64.NO_WRAP)
        }
        val newKey = ByteArray(32).also { SecureRandom().nextBytes(it) }
        encryptedPrefs.edit()
            .putString(KEY_DB_PASSPHRASE, Base64.encodeToString(newKey, Base64.NO_WRAP))
            .apply()
        return newKey
    }

    fun hasExistingKey(): Boolean = encryptedPrefs.contains(KEY_DB_PASSPHRASE)

    companion object {
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
}
