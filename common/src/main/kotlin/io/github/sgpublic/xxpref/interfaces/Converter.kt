package io.github.sgpublic.xxpref.interfaces

interface Converter<Origin, Target> {
    fun toPreference(origin: Origin): Target
    fun fromPreference(target: Target): Origin
}