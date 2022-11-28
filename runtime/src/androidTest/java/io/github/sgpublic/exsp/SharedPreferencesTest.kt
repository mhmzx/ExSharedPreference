package io.github.sgpublic.exsp

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SharedPreferencesTest {
    private val TAG = javaClass.simpleName

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val sp1 = appContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        val sp2 = appContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        assertEquals(sp1, sp2)

        val obj = Object()

        sp2.registerOnSharedPreferenceChangeListener { _, key ->
            Log.d(TAG, "OnSharedPreferenceChangeListener: $key")
            synchronized(obj) {
                obj.notify()
            }
        }

        synchronized(obj) {
            sp1.edit().putString("test_string", "test_value").apply()
            obj.wait()
        }
    }
}