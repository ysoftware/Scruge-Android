package com.scruge.scruge.model.entity

import com.scruge.scruge.dependencies.dataformatting.formatRounding

data class Balance(val symbol:String, val amount:Double): Comparable<Balance> {

    constructor(string:String) : this(string.split(" ")[1], string.split(" ")[0].toDouble())

    override fun compareTo(other: Balance): Int = compareValuesBy(other, this) { it.symbol }

    override fun toString(): String = "${amount.formatRounding(4, 4)} $symbol"
}