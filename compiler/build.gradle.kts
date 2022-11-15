plugins {
    kotlin("jvm")

    id("java-library")
    id("kotlin-kapt")
    id("maven-publish")
    id("signing")
    id("exsp-java-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    val lombokVer = "1.18.24"
    testCompileOnly("org.projectlombok:lombok:$lombokVer")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVer")
    kapt("org.projectlombok:lombok:$lombokVer")
    testImplementation("junit:junit:4.13.2")

    val autoServiceVer = "1.0.1"
    implementation("com.google.auto.service:auto-service-annotations:$autoServiceVer")
    kapt("com.google.auto.service:auto-service:$autoServiceVer")

    implementation("com.squareup:javapoet:1.13.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}