pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":common")
include(":compiler-java")
include(":compiler-kotlin")
include(":runtime")
include(":demo")
rootProject.name = "XXPreference"
