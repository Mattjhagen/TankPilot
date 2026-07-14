package com.tankpilot.android

import android.app.Application
import com.tankpilot.android.di.appModule
import com.tankpilot.di.initKoin
import org.koin.android.ext.koin.androidContext

import com.tankpilot.android.di.variantModule

class TankPilotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TankPilotApplication)
            modules(appModule, variantModule)
        }
    }
}
