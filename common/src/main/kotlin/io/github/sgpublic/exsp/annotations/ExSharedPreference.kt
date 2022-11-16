package io.github.sgpublic.exsp.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ExSharedPreference(
    val name: String,
    val mode: Int = 0x0000
)
