package com.scruge.scruge.model.entity

data class Update(val id:String, val title:String, val timestamp:Int,
                  val description:String?, val imageUrl:String,
                  val campaignInfo: CampaignInfo): Comparable<Update> {

    override fun compareTo(other: Update): Int = compareValuesBy(this, other) { it.id }
}