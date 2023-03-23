package io.github.sgpublic.xxpref

import android.content.Context
import java.lang.ref.WeakReference

object XXPref {
    private var context: WeakReference<Context>? = null

    @JvmStatic
    fun init(context: Context) {
        this.context = WeakReference(context)
    }

    @JvmStatic
    fun getSharedPreference(name: String, mode: Int): LazyPrefReference {
        return LazyPrefReference(requiredContext(), name, mode)
    }

    private fun requiredContext() = context?.get()
        ?: throw IllegalStateException("Context are not initialized, did you call ExPreference.init(context)?")
}
