package com.scruge.scruge.model.entity

interface ActivityModel:Comparable<ActivityModel> { val type:String }

// todo if needed, this should be changed
fun compareTo(t:ActivityModel, other: ActivityModel): Int = compareValuesBy(t, other) { it.type }

data class ActivityUpdate(override val type:String,
                          val update:Update,
                          val campaign:CampaignInfo):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityReply(override val type:String,
                         val replyId:String,
                         val campaignId:String,
                         val parentCommentId:String,
                         val replyCommentText:String,
                         val replyUserName:String,
                         val timestamp: Long):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityVoting(override val type:String,
                          val timestamp: Long,
                          val campaign:CampaignInfo,
                          val milestoneTitle:String,
                          val startTimestamp:Long,
                          val kind:Int,
                          val noticePeriodSec:Long):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityVotingResult(override val type:String,
                                val endTimestamp:Long,
                                val timestamp: Long,
                                val campaign:CampaignInfo,
                                val milestoneTitle:String,
                                val startTimestamp:Long,
                                val kind:Int):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityFunding(override val type:String,
                           val timestamp: Long,
                           val campaign: CampaignInfo,
                           val softCap: Double,
                           val raised:Double):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivitySubmission(override val type: String,
                              val timestamp: Long,
                              val bountyName:String,
                              val projectName:String):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivitySubmissionPaid(override val type: String,
                                  val timestamp: Long,
                                  val bountyName:String,
                                  val paidEOS:String?,
                                  val paid:String?,
                                  val projectName:String):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}