package com.svoboden.app.core.security

import java.security.MessageDigest

/**
 * УПРОЩЕНИЕ ДЛЯ MVP: соль статична для всех профилей. Для продакшена нужна
 * per-profile случайная соль, хранимая рядом с хешем (например, в ProfileEntity).
 */
object ProfilePinHasher {
    private const val STATIC_SALT = "svoboden_profile_salt"

    fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((pin + STATIC_SALT).toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun verify(pin: String, hash: String): Boolean = hash(pin) == hash
}
