package io.github.sgpublic.xxpref.demo

import android.app.Application
import io.github.sgpublic.xxpref.ExPreference

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ExPreference.init(this)
    }
}