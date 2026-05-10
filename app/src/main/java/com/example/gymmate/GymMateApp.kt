package com.example.gymmate

import android.app.Application
import com.example.gymmate.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class GymMateApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GymMateApp)
            modules(appModule)
        }
    }
}
