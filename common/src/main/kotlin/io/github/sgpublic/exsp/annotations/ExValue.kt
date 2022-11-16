package io.github.sgpublic.exsp.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class ExValue(
        val key: String = "",
        val defVal: String,
)
