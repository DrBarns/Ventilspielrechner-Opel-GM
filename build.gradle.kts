// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false

}

buildscript {
    repositories {
        mavenCentral()
        google()
        // jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")

        // classpath("com.google.dagger:hilt-android-gradle-plugin:2.37")
    }
}