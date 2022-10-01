package io.github.sgpublic.exsp.demo

import io.github.sgpublic.exsp.annotations.ExConverter
import io.github.sgpublic.exsp.interfaces.Converter
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