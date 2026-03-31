plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.vetycare"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vetycare"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Core library desugaring (necesario para Kizitonwose)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Calendario Kizitonwose (elimina prolific)
    implementation("com.kizitonwose.calendar:view:2.5.0")

    // Coroutines (necesario para el ViewModel)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel + Lifecycle (necesario para CitasViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")

    // Firebase
    /*implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-database-ktx")*/

    // Maps (
    implementation("com.mapbox.maps:android:11.20.2")
}