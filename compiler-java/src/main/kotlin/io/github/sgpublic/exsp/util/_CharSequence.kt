package io.github.sgpublic.exsp.util

import java.util.*

fun CharSequence.capitalize(): String {
    return toString().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
}