package com.scruge.scruge.viewmodel.transaction

import com.ysoftware.mvvm.array.Query
import java.lang.Math.max

class ActionsQuery:Query {

    private val batchSize = 10

    var position:Long = -1

    var offset:Long = -1

    override var size = 1

    fun setLimit(limit:Long) {
        position = limit
    }

    override fun resetPosition() {
        position = -1
        offset = -1
        size = 1
    }

    override fun advance() {
        if (position != -1L) {
            position -= size
            offset = (-batchSize).toLong()
            size = batchSize
        }
    }
}