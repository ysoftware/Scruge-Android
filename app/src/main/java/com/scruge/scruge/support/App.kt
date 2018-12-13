package com.scruge.scruge.support

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.facebook.soloader.SoLoader
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.Api

class App: Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var _context:Context? = null

        val context get() = _context!!
    }

    override fun onCreate() {
        super.onCreate()

        _context = applicationContext
        setupApp()
    }

    private fun setupApp() {
        Service.api.setEnvironment(Api.Environment.prod)
        SoLoader.init(this, false);
    }
}