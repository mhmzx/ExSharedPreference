package io.github.sgpublic.exsp.demo.java;

import java.util.Date;

import io.github.sgpublic.exsp.annotations.ExSharedPreference;
import io.github.sgpublic.exsp.annotations.ExValue;

@ExSharedPreference(name = "name_of_shared_preference")
public class JavaPreference {
    @ExValue(defVal = "test")
    private String testString;

    @ExValue(defVal = "0")
    private float testFloat;

    @ExValue(defVal = "0")
    private int testInt;

    @ExValue(defVal = "0")
    private long testLong;

    @ExValue(defVal = "false")
    private boolean testBool;

    @ExValue(defVal = "-1")
    private Date testDate;

    @ExValue(defVal = "TYPE_A")
    private Type testEnum;

    public enum Type {
        TYPE_A, TYPE_B;
    }
}