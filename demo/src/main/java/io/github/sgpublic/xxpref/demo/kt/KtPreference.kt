package io.github.sgpublic.xxpref.demo.kt

import io.github.sgpublic.xxpref.annotations.XXPreference
import io.github.sgpublic.xxpref.annotations.PrefVal
import java.util.*

@XXPreference(name = "name_of_shared_preference")
interface KtPreference {
    @PrefVal(defVal = "test")
    var testString: String

    @PrefVal(defVal = "0")
    var testFloat: Float

    @PrefVal(defVal = "0")
    var testInt: Int

    @PrefVal(defVal = "0")
    var testLong: Long

    @PrefVal(defVal = "false")
    var testBool: Boolean

    @PrefVal(defVal = "-1")
    var testDate: Date

    @PrefVal(defVal = "TYPE_A")
    var testEnum: Type

    enum class Type {
        TYPE_A, TYPE_B;
    }
}