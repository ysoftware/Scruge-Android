package com.scruge.scruge.services

import com.scruge.scruge.services.api.Api

class Service {

    companion object {

        val api = Api()

        val tokenManager = TokenManager()
    }
}