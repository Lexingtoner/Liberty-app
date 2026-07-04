package com.svoboden.app.core.security

import android.content.Context
import com.svoboden.app.data.local.AppDatabase
import com.svoboden.app.data.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

class DatabaseMigrationException(message: String, cause: Throwable) : Exception(message, cause)

/**
 * Шифрует уже существующую (незашифрованную) БД на месте при первом запуске
 * после обновления приложения. Работает НАПРЯМУЮ с файлом — до того, как
 * Room/Hilt откроют AppDatabase через SQLCipher-фабрику.
 */
@Singleton
class DatabaseEncryptionMigrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keyProvider: DatabaseKeyProvider,
    private val appPreferences: AppPreferences
) {
    suspend fun migrateIfNeeded() = withContext(Dispatchers.IO) {
        if (appPreferences.dbEncryptionDone.first()) return@withContext

        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        if (!dbFile.exists()) {
            // Свежая установка — шифровать нечего.
            appPreferences.setDbEncryptionDone(true)
            return@withContext
        }

        if (isAlreadyEncrypted(dbFile)) {
            appPreferences.setDbEncryptionDone(true)
            return@withContext
        }

        SQLiteDatabase.loadLibs(context)
        val passphrase = keyProvider.getOrCreatePassphrase()
        val encryptedFile = File(context.getDatabasePath("${AppDatabase.DATABASE_NAME}_encrypted").path)

        try {
            // ФИКС: класс из net.zetetic:android-database-sqlcipher называется
            // net.sqlcipher.database.SQLiteDatabase (НЕ android.database.sqlite.*),
            // а метод для сырых команд — execSQL (rawExecSQL не существует в этом API).
            val plainDb = SQLiteDatabase.openDatabase(
                dbFile.path, "", null, SQLiteDatabase.OPEN_READWRITE
            )
            val hexKey = passphrase.toHexString()
            plainDb.execSQL("ATTACH DATABASE '${encryptedFile.path}' AS encrypted KEY \"x'$hexKey'\"")
            plainDb.execSQL("SELECT sqlcipher_export('encrypted')")
            plainDb.execSQL("DETACH DATABASE encrypted")
            plainDb.close()

            if (!dbFile.delete()) throw IllegalStateException("Не удалось удалить исходный файл БД")
            if (!encryptedFile.renameTo(dbFile)) throw IllegalStateException("Не удалось переименовать зашифрованный файл")

            appPreferences.setDbEncryptionDone(true)
        } catch (e: Exception) {
            // Оригинал НЕ удаляем при ошибке — следующий запуск повторит попытку.
            encryptedFile.delete()
            throw DatabaseMigrationException("Не удалось зашифровать базу данных", e)
        }
    }

    private fun isAlreadyEncrypted(dbFile: File): Boolean = try {
        dbFile.inputStream().use { stream ->
            val header = ByteArray(16)
            val read = stream.read(header)
            read < 16 || String(header, Charsets.US_ASCII) != "SQLite format 3\u0000"
        }
    } catch (e: Exception) {
        false
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
}
