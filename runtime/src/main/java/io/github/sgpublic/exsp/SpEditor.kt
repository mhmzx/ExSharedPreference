package io.github.sgpublic.exsp

import android.content.SharedPreferences
import java.io.Closeable

/**
 *
 * @author Madray Haven
 * @date 2022/11/30 9:42
 */
abstract class SpEditor(
    private var editorRef: SharedPreferences.Editor?
): Closeable {
    private val editor: SharedPreferences.Editor get() {
        editorRef?.let { return it }
        throw ExspException("This editor has been saved, cannot continue editing!")
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