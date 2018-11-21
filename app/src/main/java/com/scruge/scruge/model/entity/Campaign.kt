package com.scruge.scruge.model.entity

data class CampaignInfo(val id:Int, val title:String, val imageUrl:String)

data class PartialCampaign(override val id:Int,
                           override val title:String,
                           override val description:String,
                           override val imageUrl:String,
                           override val startTimestamp:Int,
                           override val endTimestamp:Int,
                           override val economics: Economics): PartialCampaignModel

data class Campaign(override val id:Int,
                    override val title:String,
                    override val description:String,
                    override val imageUrl:String,
                    override val startTimestamp:Int,
                    override val endTimestamp:Int,
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
                    val topComments:List<Comment>?): PartialCampaignModel

interface PartialCampaignModel {

    val id:Int

    val title:String

    val description:String

    val imageUrl:String

    val startTimestamp:Int

    val endTimestamp:Int

    val economics:Economics
}