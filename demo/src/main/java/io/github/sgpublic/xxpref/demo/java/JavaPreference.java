package io.github.sgpublic.xxpref.demo.java;

import java.util.Date;

import io.github.sgpublic.xxpref.annotations.PrefVal;
import io.github.sgpublic.xxpref.annotations.XXPreference;
@XXPreference(name = "name_of_shared_preference")
public class JavaPreference {
    @PrefVal(defVal = "test")
    private String testString;

    @PrefVal(defVal = "0")
    private float testFloat;

    @PrefVal(defVal = "0")
    private int testInt;

    @PrefVal(defVal = "0")
    private long testLong;

    @PrefVal(defVal = "false")
    private boolean testBool;

    @PrefVal(defVal = "-1")
    private Date testDate;

    @PrefVal(defVal = "TYPE_A")
    private Type testEnum;

    public enum Type {
        TYPE_A, TYPE_B;
    }
}