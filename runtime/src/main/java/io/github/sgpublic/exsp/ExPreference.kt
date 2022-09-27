package io.github.sgpublic.exsp

import android.content.Context
import android.content.SharedPreferences
import java.lang.ref.WeakReference

object ExPreference {
    private var context: WeakReference<Context> = WeakReference(null)

    @JvmStatic
    fun init(context: Context) {
        this.context = WeakReference(context)
    }

    @JvmStatic
    fun getSharedPreference(name: String, mode: Int): SharedPreferences {
        return context.get()!!.getSharedPreferences(name, mode)
    }

    inline fun <reified T> get(): T {
        return get(T::class.java)
    }

    @JvmStatic
    fun <T> get(clazz: Class<T>): T {
        val target = Class.forName(clazz.name + "_Impl")
        @Suppress("UNCHECKED_CAST")
        return target.newInstance() as T
    }
}