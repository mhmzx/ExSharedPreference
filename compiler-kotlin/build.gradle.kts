import io.github.sgpublic.xxpref.Deps

plugins {
    kotlin("jvm")

    id("java-library")
    id("kotlin-kapt")
    id("maven-publish")
    id("signing")
    id("xxpref-java-publish")
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

    implementation(project(":common"))

//    // https://kotlinlang.org/docs/ksp-quickstart.html#create-a-processor-of-your-own
//    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.10-1.0.9")
    kotlin("gradle-plugin-api", Deps.Kotlin)
    kotlin("compiler-embeddable", Deps.Kotlin)

    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet
    implementation("com.squareup:kotlinpoet:1.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
