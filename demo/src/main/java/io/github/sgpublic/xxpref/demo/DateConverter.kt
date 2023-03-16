package io.github.sgpublic.xxpref.demo

import io.github.sgpublic.xxpref.annotations.ExConverter
import io.github.sgpublic.xxpref.interfaces.Converter
import java.util.*

@ExConverter
class DateConverter: Converter<Date, Long> {
    override fun toPreference(origin: Date): Long {
        return origin.time
    }

    override fun fromPreference(target: Long): Date {
        return Date(target)
    }
}