package com.demets.jas

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        AndroidThreeTen.init(this)
    }
}