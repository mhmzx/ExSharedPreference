plugins {
    kotlin("jvm")

    id("java-library")
    id("kotlin-kapt")
    id("maven-publish")
    id("signing")
    id("exsp-java-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    val autoServiceVer = "1.0.1"
    implementation("com.google.auto.service:auto-service-annotations:$autoServiceVer")
    kapt("com.google.auto.service:auto-service:$autoServiceVer")

    implementation("com.squareup:javapoet:1.13.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}