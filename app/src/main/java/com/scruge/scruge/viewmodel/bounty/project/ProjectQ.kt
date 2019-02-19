package com.scruge.scruge.viewmodel.bounty.project

import com.ysoftware.mvvm.array.Query

class ProjectQ: Query {

    var page = 0

    override fun advance() {
        page += 1
    }

    override fun resetPosition() {
        page = 0
    }
}