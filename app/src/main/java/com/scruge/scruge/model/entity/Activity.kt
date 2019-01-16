package com.scruge.scruge.model.entity

interface ActivityModel:Comparable<ActivityModel> { val type:String }

// todo if needed, this should be changed
fun compareTo(t:ActivityModel, other: ActivityModel): Int = compareValuesBy(t, other) { it.type }

data class ActivityUpdate(val update:Update, val campaign:CampaignInfo,
                          override val type:String):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityReply(val replyId:String, val campaignId:String, val replyCommentText:String,
                         val replyUserName:String, override val type:String):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityVoting(val campaign:CampaignInfo, val milestoneTitle:String, val timestamp:Long,
                          override val type:String):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}

data class ActivityFunding(override val type:String):ActivityModel {
    override fun compareTo(other: ActivityModel): Int = compareTo(this, other)
}