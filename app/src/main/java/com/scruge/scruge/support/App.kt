package com.scruge.scruge.support

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
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
        Service.api.environment = Api.Environment.prod

        Service.eos.nodeUrl = Service.settings.get<String>(Settings.Setting.nodeUrl)
                ?.let { it } ?: "https://eos.greymass.com"
    }
}