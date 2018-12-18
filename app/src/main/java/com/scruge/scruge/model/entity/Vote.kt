package com.scruge.scruge.model.entity

data class VoteResult(val voteId:Int, val active:Boolean, val positiveVotes:Int,
                      val backersCount:Int, val voters:Int, val kind:Int, val endTimestamp:Long)

data class Voting(val campaign: CampaignInfo, val voting: VoteInfo)

data class VoteInfo(val kind: Int, val voteId: Int, val endTimestamp: Long)

enum class VoteKind(val kind:Int) {

    extend(0), milestone(1);

    companion object {
        fun from(kind:Int):VoteKind = if (kind == 0) extend else milestone
    }
}