package io.github.sgpublic.xxpref.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class PrefVal(
    val key: String = "",
    val defVal: String,
)
