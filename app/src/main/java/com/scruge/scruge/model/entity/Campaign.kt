package com.scruge.scruge.model.entity

data class CampaignInfo(val id:Int, val title:String, val imageUrl:String)

data class PartialCampaign(override val id:Int,
                           override val title:String,
                           override val description:String,
                           override val imageUrl:String,
                           override val startTimestamp:Long,
                           override val endTimestamp:Long,
                           override val economics: Economics
                          ): PartialCampaignModel, Comparable<PartialCampaign> {

    override fun compareTo(other: PartialCampaign): Int = compareValuesBy(this, other,
                                                                          { it.id }, { it.economics })
}

data class Campaign(override val id:Int,
                    override val title:String,
                    override val description:String,
                    override val imageUrl:String,
                    override val startTimestamp:Long,
                    override val endTimestamp:Long,
                    override val economics: Economics,

                    // Full campaign only

                    val team:List<Member>,
                    val status:Int,
                    val about:String?,
                    val videoUrl:String, // video
                    val totalCommentsCount:Int,
                    val	social:List<Social>?,
                    val faq:List<Faq>?,
                    val documents:List<Document>?,
                    val currentMilestone:Milestone?,
                    val lastUpdate:Update?,
                    val topComments:List<Comment>?,
                    val pitchUrl:String?): PartialCampaignModel, Comparable<Campaign> {

    override fun compareTo(other: Campaign): Int {
        return if (topComments != other.topComments) -1
        else compareValuesBy(this, other, { it.id }, { it.economics }, { it.status }, { it.lastUpdate },
                             { it.currentMilestone })
    }
}

interface PartialCampaignModel {

    val id:Int

    val title:String

    val description:String

    val imageUrl:String

    val startTimestamp:Long

    val endTimestamp:Long

    val economics:Economics
}