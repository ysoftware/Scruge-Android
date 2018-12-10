package com.scruge.scruge.viewmodel.comment

import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.entity.Update
import com.ysoftware.mvvm.array.Query

enum class CommentSource {
    update, campaign;

    var updateObject: Update? = null
    var campaignObject: Campaign? = null
}

class CommentQuery(val source: CommentSource) : Query {

    var page = 0

    override fun advance() {
        page += 1
    }

    override fun resetPosition() {
        page = 0
    }
}