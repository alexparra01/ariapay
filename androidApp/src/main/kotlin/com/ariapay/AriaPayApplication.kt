package com.ariapay

import android.app.Application
import com.ariapay.di.appModule
import com.ariapay.di.getSharedKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AriaPayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AriaPayApplication)
            modules(getSharedKoinModules() + appModule)
        }
    }
}
