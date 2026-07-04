# SQLCipher — обязательно, иначе release-сборка падает при открытии БД
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }
-dontwarn net.sqlcipher.**

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.svoboden.app.**$$serializer { *; }
-keepclassmembers class com.svoboden.app.** { *** Companion; }
-keepclasseswithmembers class com.svoboden.app.** { kotlinx.serialization.KSerializer serializer(...); }
