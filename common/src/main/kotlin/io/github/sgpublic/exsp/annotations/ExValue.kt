package io.github.sgpublic.exsp.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ExValue(
        val key: String = "",
        val defVal: String,
)
