package io.github.sgpublic.exsp.utils

object OsCheck {
    fun isWindows(): Boolean {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }
}