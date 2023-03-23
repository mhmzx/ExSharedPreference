package io.github.sgpublic.xxpref

import android.content.SharedPreferences
import java.io.Closeable

abstract class PrefEditor(
    private var editorRef: SharedPreferences.Editor?
): Closeable {
    private val editor: SharedPreferences.Editor get() {
        editorRef?.let { return it }
        throw XXPrefException("This editor has been saved, cannot continue editing!")
    }

    protected fun putString(key: String, value: String) {
        editor.putString(key, value)
    }

    protected fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
    }

    protected fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
    }

    protected fun putLong(key: String, value: Long) {
        editor.putLong(key, value)
    }

    protected fun putFloat(key: String, value: Float) {
        editor.putFloat(key, value)
    }

    fun apply() {
        editor.apply()
    }

    override fun close() {
        apply()
    }
}