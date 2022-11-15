package io.github.sgpublic.exsp

import android.content.Context
import android.content.SharedPreferences
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

object ExPreference {
    private var context: WeakReference<Context>? = WeakReference(null)

    @JvmStatic
    fun init(context: Context) {
        this.context = WeakReference(context)
    }

    @JvmStatic
    fun getSharedPreference(name: String, mode: Int): Reference {
        return Reference(context?.get()!!, name, mode)
    }

    class Reference(
        private val context: Context,
        private val name: String,
        private val mode: Int,
    ) {
        private var SharedPreferences: SharedPreferences? = null
        fun get(): SharedPreferences {
            synchronized(this) {
                if (SharedPreferences == null) {
                    SharedPreferences = context.getSharedPreferences(name, mode)
                }
                return SharedPreferences!!
            }
        }
        fun clear() {
            synchronized(this) {
                if (SharedPreferences != null) {
                    SharedPreferences = null
                }
            }
        }
    }

    inline fun <reified T> get(): T {
        return get(T::class.java)
    }

    @JvmStatic
    fun <T> get(clazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return (Class.forName("io.github.sgpublic.exsp.ExPrefs")
            .getMethod("get", Class::class.java).invoke(null, clazz) as Lazy<T>).value
    }
}

operator fun ExPreference.Reference.getValue(thisRef: Any?, property: KProperty<*>): SharedPreferences {
    return this.get()
}