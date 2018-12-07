package com.scruge.scruge.model.entity

data class Milestone(val id: String, val title: String, val endTimestamp: Long, val description: String,
                     val fundsReleasePercent: Double):Comparable<Milestone> {

    override fun compareTo(other: Milestone): Int = compareValuesBy(this, other) { it.id }
}