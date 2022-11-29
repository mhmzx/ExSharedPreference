package io.github.sgpublic.exsp.interfaces

/**
 *
 * @author Madray Haven
 * @date 2022/11/29 10:32
 */
interface SharedPreferenceListener {
    fun onChange(key: String, value: Any)
}