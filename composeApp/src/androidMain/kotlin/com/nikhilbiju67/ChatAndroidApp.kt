package com.nikhilbiju67


import android.app.Application
import android.content.Context

class ChatAndroidApp : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

}