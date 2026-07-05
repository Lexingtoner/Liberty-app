plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // ФИКС: отсутствовал в исходном плане, а kotlinx.serialization.json уже использовался в ExportDataUseCase/ImportDataUseCase — без плагина проект не соберётся.
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.svoboden.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.svoboden.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-MVP"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            versionNameSuffix = rootProject.extra["sourceCompatibility"].toString()
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra["sourceCompatibility"] as JavaVersion
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    ndkVersion = "30.0.14904198 rc1"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ── Compose ──────────────────────────────────────────────
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.activity)
    debugImplementation(libs.compose.ui.tooling)

    // ── Navigation ───────────────────────────────────────────
    implementation(libs.navigation.compose)

    // ── Hilt (DI) ────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler) // ФИКС: раньше не подключался компилятор для @HiltWorker — без него ReminderWorker не сгенерируется.

    // ── Room (локальная БД) ──────────────────────────────────
    // ВАЖНО: room-runtime подключаем, но фактический SQLiteOpenHelper подменяется
    // на SQLCipher через openHelperFactory() в DatabaseModule — сам room-runtime
    // нужен для остальной инфраструктуры Room (компилятор, ktx, аннотации).
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // ── DataStore (настройки/preferences) ────────────────────
    implementation(libs.datastore.preferences)

    // ── Coroutines ───────────────────────────────────────────
    implementation(libs.coroutines.android)

    // ── Serialization (экспорт JSON) ─────────────────────────
    implementation(libs.serialization.json)

    // ── WorkManager (уведомления) ─────────────────────────────
    implementation(libs.work.runtime.ktx)

    // ── Vico (графики) ────────────────────────────────────────
    implementation(libs.vico.compose.m3)

    // ── Биометрия и шифрование ────────────────────────────────
    implementation(libs.biometric)
    implementation(libs.security.crypto)
    implementation(libs.sqlcipher.android)
    implementation(libs.sqlite.bridge)

    // ── Splash Screen API ─────────────────────────────────────
    implementation(libs.splashscreen)

    // ── Glance (виджет) ────────────────────────────────────────
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    // ── Tests ────────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}
