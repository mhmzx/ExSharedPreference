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

    implementation("com.squareup:javapoet:1.13.0")

    implementation(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"
    )
//    options.isFork = true
//    options.forkOptions.jvmArgs = listOf(
//        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
//        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
//        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"
//    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}