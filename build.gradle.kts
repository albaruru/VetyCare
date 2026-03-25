// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle.kts (raíz, no el de app)
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}