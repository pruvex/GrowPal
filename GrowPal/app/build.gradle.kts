// D:\Android\Projekte\GrowPal\GrowPal\app\build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "de.Pruvex.growpal"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.Pruvex.growpal"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true // Stellt sicher, dass Compose aktiviert ist
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinComposeCompiler.get()
    }
}

dependencies {
    // Core AndroidX Bibliotheken
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat) // Wird oft indirekt benötigt

    // Compose Bibliotheken
    implementation(platform(libs.androidx.compose.bom)) // Wichtig: Definiert Compose-Versionen
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)

    // *** NEU HINZUGEFÜGTE BIBLIOTHEKEN WERDEN HIER VERWENDET ***
    implementation(libs.androidx.activity.ktx)          // <- Diese Zeile ist jetzt hier drin!
    implementation(libs.androidx.lifecycle.viewmodel.compose) // <- Diese Zeile ist jetzt hier drin!
    implementation(libs.androidx.core.splashscreen)    // <- Diese Zeile ist jetzt hier drin!

    // Firebase
    implementation(platform(libs.firebase.bom)) // Wichtig: Definiert Firebase-Versionen
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Test Bibliotheken
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose-Test-Versionen
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling) // Für Compose Preview etc. im Debug-Build
    debugImplementation(libs.androidx.ui.test.manifest)
}