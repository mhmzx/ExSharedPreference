plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    id("maven-publish")
    id("signing")
    id("exsp-android-publish")
}

android {
    namespace = "io.github.sgpublic.exsp"
    compileSdk = 33
    buildToolsVersion = "33.0.0"

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }

    defaultConfig {
        minSdk = 16
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":annotation"))

    api("org.projectlombok:lombok:1.18.24")
}