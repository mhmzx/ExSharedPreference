import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.type

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

include(":common")
include(":compiler-java")
include(":compiler-kotlin")
include(":runtime")
include(":demo")

rootProject.name = "XXPreference"
