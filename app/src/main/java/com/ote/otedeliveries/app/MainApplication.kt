package com.ote.otedeliveries.app

import android.app.Application
import android.content.Context
import com.google.android.libraries.places.api.Places
import com.ote.otedeliveries.utils.API_KEY
import net.danlew.android.joda.JodaTimeAndroid

class MainApplication : Application() {
    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
        JodaTimeAndroid.init(this)
        Places.initialize(this, API_KEY)

    }


    companion object {

        private var INSTANCE: MainApplication? = null

        fun applicationContext(): Context {
            return INSTANCE!!.applicationContext
        }

    }
}