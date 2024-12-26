package com.turnit.app.auth

import android.app.Application
import com.backendless.Backendless

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Backendless.initApp(
            applicationContext,
            "89ED8037-694F-4ABD-9CAB-F9EAA8EF7A83", // Application ID
            "C11383F8-2964-4A4A-8FB2-55F4FE0B2739"         // API Key
        )
    }
}
