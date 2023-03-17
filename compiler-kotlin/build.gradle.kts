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

    val autoServiceVer = "1.0.1"
    implementation("com.google.auto.service:auto-service-annotations:$autoServiceVer")
    kapt("com.google.auto.service:auto-service:$autoServiceVer")

    implementation(project(":common"))

    // https://kotlinlang.org/docs/ksp-quickstart.html#create-a-processor-of-your-own
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")

    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet
    implementation("com.squareup:kotlinpoet:1.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
