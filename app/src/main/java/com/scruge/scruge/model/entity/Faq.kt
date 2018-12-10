package com.scruge.scruge.model.entity

data class Faq(val question:String, val answer:String):Comparable<Faq> {

    override fun compareTo(other: Faq): Int = compareValuesBy(this, other) { it.question }
}