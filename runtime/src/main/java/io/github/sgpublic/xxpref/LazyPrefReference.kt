package io.github.sgpublic.xxpref

import android.content.Context
import android.content.SharedPreferences
import io.github.sgpublic.xxpref.interfaces.SharedPreferenceListener

class LazyPrefReference internal constructor(
    private val context: () -> Context,
    private val name: String,
    private val mode: Int,
): SharedPreferences.OnSharedPreferenceChangeListener, Lazy<SharedPreferences> {
    private var sp: SharedPreferences? = null

    override val value: SharedPreferences get() {
        sp?.let { return it }
        synchronized(this) {
            sp?.let { return it }
            sp = context.invoke().getSharedPreferences(name, mode)
            sp?.registerOnSharedPreferenceChangeListener(this)
            return sp!!
        }
    }

    fun edit(): SharedPreferences.Editor {
        return value.edit()
    }

    private var listener: SharedPreferenceListener? = null
    fun setOnSharedPreferenceChangedListener(listener: SharedPreferenceListener?) {
        this.listener = listener
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
        listener?.onChange(key, sp.all[key]!!)
    }

    override fun isInitialized(): Boolean = sp != null

    fun put(key: String, value: Any) {
        this.value.edit().let {
            when (value) {
                is String -> it.putString(key, value)
                is Int -> it.putInt(key, value)
                is Float -> it.putFloat(key, value)
                is Boolean -> it.putBoolean(key, value)
                is Long -> it.putLong(key, value)
                else -> throw XXPrefException("Unsupport type: ${value.javaClass}")
            }
            it.apply()
        }
    }

    fun <T: Any> get(key: String, defVal: T): T {
        @Suppress("UNCHECKED_CAST")
        return when (defVal) {
            is String -> value.getString(key, defVal)!!
            is Int -> value.getInt(key, defVal)
            is Float -> value.getFloat(key, defVal)
            is Boolean -> value.getBoolean(key, defVal)
            is Long -> value.getLong(key, defVal)
            else -> throw XXPrefException("Unsupported type: ${defVal.javaClass}")
        } as T
    }


    fun <T: Enum<T>> getEnum(clazz: Class<T>, key: String, defVal: String): T {
        val valueOf = clazz.getDeclaredMethod("valueOf", String::class.java)
        @Suppress("UNCHECKED_CAST")
        return try {
            valueOf.invoke(null, value.getString(key, defVal))
        } catch (e: Exception) {
            valueOf.invoke(null, defVal)
        } as T
    }


    inline fun <reified T: Enum<T>> getEnum(key: String, defVal: String): T {
        return getEnum(T::class.java, key, defVal)
    }
}