import io.github.sgpublic.xxpref.Deps

plugins {
    kotlin("jvm")

    id("java-library")
    id("kotlin-kapt")
    id("maven-publish")
    id("signing")
    id("io.github.sgpublic.java-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation("com.google.auto.service:auto-service-annotations:${Deps.AutoService}")
    kapt("com.google.auto.service:auto-service:${Deps.AutoService}")

    implementation("com.squareup:javapoet:1.13.0")

    implementation(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}