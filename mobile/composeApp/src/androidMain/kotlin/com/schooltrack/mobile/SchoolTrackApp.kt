package com.schooltrack.mobile

import android.app.Application
import com.schooltrack.mobile.di.appModule
import org.koin.core.context.startKoin

class SchoolTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}
