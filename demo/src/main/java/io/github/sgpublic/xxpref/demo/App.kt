package io.github.sgpublic.xxpref.demo

import android.app.Application
import io.github.sgpublic.xxpref.XXPref

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        XXPref.init(this)
    }
}