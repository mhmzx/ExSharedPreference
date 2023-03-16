package io.github.sgpublic.xxpref.utils

object OsCheck {
    fun isWindows(): Boolean {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }
}