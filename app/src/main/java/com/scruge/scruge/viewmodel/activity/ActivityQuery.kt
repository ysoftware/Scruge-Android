package com.scruge.scruge.viewmodel.activity

import com.ysoftware.mvvm.array.Query

class ActivityQ: Query {

    var page = 0

    override fun resetPosition() {
        page = 0
    }

    override fun advance() {
        page += 1
    }
}