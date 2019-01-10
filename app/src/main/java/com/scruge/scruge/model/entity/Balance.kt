package com.scruge.scruge.model.entity

import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.services.eos.Token

data class Balance(val token: Token, val amount:Double): Comparable<Balance> {

    constructor(string:String, contract:String)
            : this(Token(contract, string.split(" ")[1]), string.split(" ")[0].toDouble())

    override fun compareTo(other: Balance): Int = compareValuesBy(other, this) { it.token.symbol }

    override fun toString(): String = "${amount.formatRounding(4, 4)} ${token.symbol}"
}