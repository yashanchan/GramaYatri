// app/build.gradle.kts
// This file lists every library your app uses.

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Google Services plugin — processes google-services.json for Firebase
    id("com.google.gms.google-services")
    // KSP — needed by Room to generate database code automatically
    id("com.google.devtools.ksp") version "2.1.10-1.0.31"
}

android {
    namespace  = "com.transit.gramayatri"
    compileSdk = 35

    defaultConfig {
        applicationId   = "com.transit.gramayatri"
        minSdk          = 26
        targetSdk       = 35
        versionCode     = 1
        versionName     = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ── Core Android ────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ── Jetpack Compose ──────────────────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Extended Material icons (bus icon, campaign icon, etc.)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // ── Navigation (single-activity Compose navigation) ──────────────────────
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // ── ViewModel + LiveData ─────────────────────────────────────────────────
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // ── Room (local offline database) ────────────────────────────────────────
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    // ── Firebase ─────────────────────────────────────────────────────────────
    // Firebase BOM — manages all Firebase library versions together
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-database-ktx")   // Realtime Database
    implementation("com.google.firebase:firebase-auth-ktx")       // Authentication
    implementation("com.google.firebase:firebase-messaging-ktx")  // Push notifications

    // ── Coroutines (for async Firebase calls) ────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // ── Testing ──────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}