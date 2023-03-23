package io.github.sgpublic.xxpref.demo.java;

import java.util.Date;

import io.github.sgpublic.xxpref.annotations.PrefConverter;
import io.github.sgpublic.xxpref.interfaces.Converter;

@PrefConverter
public class DateConverterJava implements Converter<Date, Long> {
    @Override
    public Long toPreference(Date origin) {
        return origin.getTime();
    }

    @Override
    public Date fromPreference(Long target) {
        return new Date(target);
    }
}