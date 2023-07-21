plugins {
    id("com.android.library") version io.github.sgpublic.xxpref.Deps.Android apply false
    id("com.android.application") version io.github.sgpublic.xxpref.Deps.Android apply false

    kotlin("android") version io.github.sgpublic.xxpref.Deps.Kotlin apply false
    id("com.google.devtools.ksp") version io.github.sgpublic.xxpref.Deps.Ksp apply false

    id("io.github.sgpublic.android-publish") version io.github.sgpublic.xxpref.Deps.Publish apply false
    id("io.github.sgpublic.java-publish") version io.github.sgpublic.xxpref.Deps.Publish apply false
}