package io.github.sgpublic.xxpref.jc

import io.github.sgpublic.xxpref.XXPrefProcessor
import kotlin.reflect.KClass

private val ProcessEnvContext: Any by lazy {
    Class.forName("com.sun.tools.javac.processing.JavacProcessingEnvironment")
        .getMethod("getContext").invoke(XXPrefProcessor.PROCESS_ENV)
}

fun <T: Any> KClass<T>.instanceWithContext(): T {
    @Suppress("UNCHECKED_CAST")
    return java.instanceWithContext()
}
fun <T: Any> KClass<T>.lazyInstanceWithContext(): Lazy<T> {
    return java.lazyInstanceWithContext()
}

fun <T: Any> Class<T>.instanceWithContext(): T {
    @Suppress("UNCHECKED_CAST")
    return getMethod("instance", Class.forName("com.sun.tools.javac.util.Context"))
        .invoke(null, ProcessEnvContext) as T
}
fun <T: Any> Class<T>.lazyInstanceWithContext(): Lazy<T> {
    return lazy { instanceWithContext() }
}