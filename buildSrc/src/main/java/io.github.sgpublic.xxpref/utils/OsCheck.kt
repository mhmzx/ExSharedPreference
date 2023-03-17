package io.github.sgpublic.xxpref.utils

object OsCheck {
    fun isWindows(): Boolean {
        return System.getProperties().getProperty("os.name").uppercase().indexOf("WINDOWS") != -1;
    }
}