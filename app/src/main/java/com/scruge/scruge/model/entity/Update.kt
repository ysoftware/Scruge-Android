package com.scruge.scruge.model.entity

data class Update(val id:String, val title:String, val timestamp:Long,
                  val description:String?, val imageUrl:String,
                  var campaignInfo: CampaignInfo): Comparable<Update> {

    override fun compareTo(other: Update): Int = compareValuesBy(this, other) { it.id }
}

data class Activity(val update:Update, val campaign:CampaignInfo)