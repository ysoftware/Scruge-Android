package com.scruge.scruge.model.entity

data class VoteResult(val voteId:Int, val active:Boolean, val positiveVotes:Int,
                      val backersCount:Int, val voters:Int, val kind:Int, val endTimestamp:Int)

data class Voting(val campaign: CampaignInfo, val voting: VoteInfo)

data class VoteInfo(val kind: Int, val voteId: Int, val endTimestamp: Int)