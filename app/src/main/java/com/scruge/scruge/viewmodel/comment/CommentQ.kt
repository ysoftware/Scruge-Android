package com.scruge.scruge.viewmodel.comment

import com.ysoftware.mvvm.array.Query

class CommentQuery(val source: Source, val id:String) : Query {

    enum class Source {
        update, campaign
    }

    var page = 0

    override fun advance() {
        page += 1
    }

    override fun resetPosition() {
        page = 0
    }
}