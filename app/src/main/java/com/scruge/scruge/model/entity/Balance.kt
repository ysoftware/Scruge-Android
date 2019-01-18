package com.scruge.scruge.model.entity

import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.services.eos.Token

// todo rename as this is used to represent any amount of crypto currency
data class Balance(val token: Token, val amount:Double): Comparable<Balance> {

    override fun compareTo(other: Balance): Int = compareValuesBy(other, this) { it.token.symbol }

    override fun toString(): String = "${amount.formatRounding(4, 4)} ${token.symbol}"

    companion object {

        fun from(balance:String, contract: EosName):Balance? {
            val array = balance.split(" ")
            if (array.size == 2) {
                val amount = array[0].toDoubleOrNull()
                if (amount != null) {
                    val token = Token(contract, array[1])
                    return Balance(token, amount)
                }
            }
            return null
        }
    }
}