package com.scruge.scruge.model.entity

data class Economics(val hardCap:Int, val softCap:Int, val raised:Double,
                     val publicTokenPercent:Double, val tokenSupply:Double,
                     val annualInflationPercent:Range,
                     val minUserContribution:Double, val maxUserContribution:Double,
                     val initialFundsReleasePercent:Double): Comparable<Economics> {

    override fun compareTo(other: Economics): Int = compareValuesBy(this, other) { it.raised }
}