package io.github.sgpublic.xxpref.demo.kt

import io.github.sgpublic.xxpref.annotations.ExSharedPreference
import io.github.sgpublic.xxpref.annotations.ExValue
import java.util.*

@ExSharedPreference(name = "name_of_shared_preference")
interface KtPreference {
    @ExValue(defVal = "test")
    var testString: String

    @ExValue(defVal = "0")
    var testFloat: Float

    @ExValue(defVal = "0")
    var testInt: Int

    @ExValue(defVal = "0")
    var testLong: Long

    @ExValue(defVal = "false")
    var testBool: Boolean

    @ExValue(defVal = "-1")
    var testDate: Date

    @ExValue(defVal = "TYPE_A")
    var testEnum: Type

    enum class Type {
        TYPE_A, TYPE_B;
    }
}