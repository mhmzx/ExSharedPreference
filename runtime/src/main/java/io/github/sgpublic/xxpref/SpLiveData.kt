package io.github.sgpublic.xxpref

import androidx.lifecycle.MutableLiveData

/**
 *
 * @author Madray Haven
 * @date 2022/11/29 10:41
 */
class SpLiveData<T>(defVal: T, val name: String): MutableLiveData<T>(defVal) {
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SpLiveData<*>) {
            return false
        }
        return name == other.name
    }
}