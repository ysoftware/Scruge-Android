package com.scruge.scruge.viewmodel.comment

import com.ysoftware.mvvm.array.Query

enum class CommentSource {
    update, campaign
}

class CommentQuery(val source: CommentSource, val id:String) : Query {

    var page = 0

    override fun advance() {
        page += 1
    }

    override fun resetPosition() {
        page = 0
    }
}