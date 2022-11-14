package io.github.sgpublic.exsp

import android.content.Context
import android.content.SharedPreferences
import java.lang.ref.WeakReference

object ExPreference {
    private var context: WeakReference<Context>? = WeakReference(null)

    @JvmStatic
    fun init(context: Context) {
        this.context = WeakReference(context)
    }

    @JvmStatic
    fun getSharedPreference(name: String, mode: Int): SharedPreferences {
        return context?.get()!!.getSharedPreferences(name, mode)
    }

    inline fun <reified T> get(): T {
        return get(T::class.java)
    }

    @JvmStatic
    fun <T> get(clazz: Class<T>): T {
        val target = Class.forName("io.github.sgpublic.exsp.ExPrefs")
        @Suppress("UNCHECKED_CAST")
        val result by target.getMethod("get", Class::class.java).invoke(null, clazz) as Lazy<T>
        return result
    }
}