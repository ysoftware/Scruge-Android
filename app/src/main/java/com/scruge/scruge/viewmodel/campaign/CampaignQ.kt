package com.scruge.scruge.viewmodel.campaign

import com.ysoftware.mvvm.array.Query

class CampaignQuery : Query {

    var page = 0

    override fun advance() {
        page += 1
    }

    override fun resetPosition() {
        page = 0
    }

    enum class RequestType {
        regular, backed, subscribed
    }

    var requestType = RequestType.regular

//    var category:CategoryVM?

    var query:String? = null

    var tags:List<String>? = null

    var type:String? = null
}