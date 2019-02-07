package com.scruge.scruge.model.entity

data class Producer(val producer: com.memtrip.eos.http.rpc.model.producer.response.Producer):Comparable<Producer> {

    override fun compareTo(other: Producer): Int = compareValuesBy(this, other) { it.producer.owner }
}