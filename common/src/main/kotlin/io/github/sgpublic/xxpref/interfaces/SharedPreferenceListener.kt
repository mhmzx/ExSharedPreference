package io.github.sgpublic.xxpref.interfaces

/**
 *
 * @author Madray Haven
 * @date 2022/11/29 10:32
 */
interface SharedPreferenceListener {
    fun onChange(key: String, value: Any)
}