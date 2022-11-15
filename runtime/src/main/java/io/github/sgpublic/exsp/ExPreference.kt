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
        return Reference(requiredContext(), name, mode)
    }

    private fun requiredContext() =
        context?.get() ?: throw IllegalStateException("Context are not initialized, did you call ExPreference.init(context)?")

    class Reference internal constructor(
        private val context: Context,
        private val name: String,
        private val mode: Int,
    ) {
        private var sp: SharedPreferences? = null
        fun get(): SharedPreferences {
            sp?.let { return it }
            context.getSharedPreferences(name, mode).let {
                sp = it
                it.registerOnSharedPreferenceChangeListener(object :
                    SharedPreferences.OnSharedPreferenceChangeListener {
                    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
                        it.unregisterOnSharedPreferenceChangeListener(this)
                        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
                        sp = sharedPreferences
                    }
                })
                return it
            }
        }

        fun edit(): SharedPreferences.Editor {
            return get().edit()
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
    return get()
}