package io.github.sgpublic.compiler

import org.junit.Test

/**
 *
 * @author Madray Haven
 * @date 2022/11/15 14:09
 */
class LazyKotlinTest {
    private val lazy = lazy {
        println("initialized!")
        114514
    }

    @Test
    fun test() {
        println("is initialized: " + lazy.isInitialized())
        println("value: " + lazy.value)
        println("is initialized: " + lazy.isInitialized())
    }
}