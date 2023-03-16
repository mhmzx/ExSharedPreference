plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    id("maven-publish")
    id("signing")
    id("xxpref-android-publish")
}

android {
    namespace = "io.github.sgpublic.xxpref"
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-livedata-core
    api("androidx.lifecycle:lifecycle-livedata-core:2.6.0")

    implementation(project(":common"))
}