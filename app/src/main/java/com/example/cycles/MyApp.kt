package com.example.cycles


import android.app.Application
import com.example.cycles.viewmodel.SessionCache
import dagger.hilt.android.HiltAndroidApp

//punto de arranque de hilt (framework de google para facilitar la inyeccion de dependencias)
@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionCache.init(this)
    }
}
