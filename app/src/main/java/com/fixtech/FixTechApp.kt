package com.fixtech

import android.app.Application
import com.fixtech.di.AppContainer

class FixTechApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)
    }

    companion object {
        @Volatile
        var instance: FixTechApp? = null
            private set
    }
}
