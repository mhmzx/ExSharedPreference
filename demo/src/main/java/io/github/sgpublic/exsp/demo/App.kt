package io.github.sgpublic.exsp.demo

import android.app.Application
import io.github.sgpublic.exsp.ExPreference

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ExPreference.init(this)
    }
}