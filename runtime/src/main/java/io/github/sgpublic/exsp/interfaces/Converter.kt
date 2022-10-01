package io.github.sgpublic.exsp.interfaces

interface Converter<Origin, Target> {
    fun toPreference(origin: Origin): Target
    fun fromPreference(target: Target): Origin
}