package com.scruge.scruge.services

import com.scruge.scruge.services.api.Api
import com.scruge.scruge.view.main.Presenter

class Service {

    companion object {

        val api = Api()

        val tokenManager = TokenManager()

        val presenter = Presenter()
    }
}