// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.2.1")
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.4.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.sumup.com/releases") }
    }
}
