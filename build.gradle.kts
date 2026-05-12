// Project-level build.gradle.kts
// This file tells Gradle which plugin versions to use across the whole project.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android)      apply false
    alias(libs.plugins.kotlin.compose)      apply false
    // Google Services plugin — needed for Firebase
    id("com.google.gms.google-services")    version "4.4.4" apply false

}