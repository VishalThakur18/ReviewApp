buildscript {
    val agp_version by extra("8.2.2")
    dependencies {
        classpath ("com.android.tools.build:gradle:8.0.0")
        classpath("com.google.gms:google-services:4.4.2")
    }
    repositories {
        maven("https://www.jitpack.io")
        mavenCentral()

    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}