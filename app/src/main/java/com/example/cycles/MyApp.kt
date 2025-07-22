package com.example.cycles


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//punto de arranque de hilt (framework de google para facilitar la inyeccion de dependencias)
@HiltAndroidApp
class MyApp : Application()