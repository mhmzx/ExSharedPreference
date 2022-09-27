package io.github.sgpublic.exsp.demo;

import io.github.sgpublic.exsp.annotations.ExSharedPreference;
import io.github.sgpublic.exsp.annotations.ExValue;
import lombok.Data;

@Data
@ExSharedPreference(name = "test")
public class TestPreference {
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
}
