plugins {
    val androidVer = "7.3.1"
    id("com.android.library") version androidVer apply false
    id("com.android.application") version androidVer apply false

    val ktVer = "1.7.20"
    id("org.jetbrains.kotlin.android") version ktVer apply false
}