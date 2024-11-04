// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("jvm") version "1.9.24" apply false
}

buildscript {
    dependencies{
        val nav_versión = "2.8.2"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_versión")
    }
}