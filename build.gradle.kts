plugins {
    val androidVer = "7.4.2"
    id("com.android.library") version androidVer apply false
    id("com.android.application") version androidVer apply false

    val ktVer = "1.8.10"
    id("org.jetbrains.kotlin.android") version ktVer apply false
}