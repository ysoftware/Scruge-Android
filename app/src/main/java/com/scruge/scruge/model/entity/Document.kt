package com.scruge.scruge.model.entity

data class Document(val name:String, val url:String): Comparable<Document> {

    override fun compareTo(other: Document):Int = compareValuesBy(this, other) { it.url }
}