package com.example.musicrental

import android.app.Application
import com.example.musicrental.util.Prefs

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)
    }
}