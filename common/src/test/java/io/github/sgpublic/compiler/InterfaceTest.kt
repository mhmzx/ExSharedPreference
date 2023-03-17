package io.github.sgpublic.compiler

import io.github.sgpublic.xxpref.annotations.PrefVal

interface TestPreference {
    @PrefVal(defVal = "false")
    var testBoolean: Boolean
    companion object: TestPreference {
        override var testBoolean: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
    }
}